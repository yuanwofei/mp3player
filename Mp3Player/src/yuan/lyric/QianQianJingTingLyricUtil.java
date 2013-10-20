package yuan.lyric;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**千千静听歌词处理工具类*/
public class QianQianJingTingLyricUtil {
	public static final String SearchPath = "http://ttlrcct2.qianqian.com/dll/lyricsvr.dll?sh?Artist={0}&Title={1}&Flags=0";
	public static final String DownloadPath = "http://ttlrcct2.qianqian.com/dll/lyricsvr.dll?dl?Id={0}&Code={1}";

	public static String fetchLyricContent(int lrcId, String lrcCode) {
		String url = MessageFormat.format("http://ttlrcct2.qianqian.com/dll/lyricsvr.dll?dl?Id={0}&Code={1}", 
				new Object[] {(new StringBuilder()).append("").append(lrcId).toString(), lrcCode});		
		return readURL(url);
	}

	public static List<QianQianJingTingLyricInfo> search(String artist, String title)throws Exception{		
		artist = artist.toLowerCase(Locale.getDefault()).replace(" ", "").replace("'", "");
		title = title.toLowerCase(Locale.getDefault()).replace(" ", "").replace("'", "");
		String url = MessageFormat.format("http://ttlrcct2.qianqian.com/dll/lyricsvr.dll?sh?Artist={0}&Title={1}&Flags=0", new Object[] {
			ToQianQianHexString(artist, "UTF-16LE"), ToQianQianHexString(title, "UTF-16LE")
		});
		String back = readURL(url);
		
		//使用DOM（文档对象模型）来解析XML文件
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(back)));
		NodeList nodes = doc.getElementsByTagName("lrc");
		List<QianQianJingTingLyricInfo> list = new ArrayList<QianQianJingTingLyricInfo>();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String artistTemp = node.getAttributes().getNamedItem("artist").getTextContent();
			String titleTemp = node.getAttributes().getNamedItem("title").getTextContent();
			final int lrcId = Integer.parseInt(node.getAttributes().getNamedItem("id").getTextContent());
			final String lrcCode = CreateQianQianCode(artistTemp, titleTemp, lrcId);
			QianQianJingTingLyricInfo.Task task = new QianQianJingTingLyricInfo.Task() {
				public String getLyricContent() {						
					return QianQianJingTingLyricUtil.fetchLyricContent(lrcId, lrcCode);
				}
			};
			QianQianJingTingLyricInfo result = new QianQianJingTingLyricInfo(lrcId, lrcCode, artistTemp, titleTemp, task);
			list.add(result);
		}

		return list;
	}

	public static String readURL(String url) {
		StringBuilder sb;
		try {
			HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
			conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0.1) Gecko/20100101 Firefox/8.0.1");
			conn.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String temp = null;
			sb = new StringBuilder();
			while ((temp = br.readLine()) != null)
				sb.append(temp).append("\n");
			br.close();
		} catch (IOException e) {
			return null;
		}		
		return sb.toString();
	}

	private static String ToQianQianHexString(String s, String chatset)
		throws UnsupportedEncodingException
	{
		StringBuilder sb = new StringBuilder();
		byte bytes[] = s.getBytes(chatset);
		byte arr[] = bytes;
		int len = arr.length;
		for (int i = 0; i < len; i++)
		{
			byte b = arr[i];
			int j = b & 0xff;
			if (j < 16)
				sb.append("0");
			sb.append(Integer.toString(j, 16).toUpperCase(Locale.getDefault()));
		}

		return sb.toString();
	}

	private static String CreateQianQianCode(String singer, String title, int lrcId)
		throws UnsupportedEncodingException
	{
		String qqHexStr = ToQianQianHexString((new StringBuilder()).append(singer).append(title).toString(), "UTF-8");
		int length = qqHexStr.length() / 2;
		int song[] = new int[length];
		for (int i = 0; i < length; i++)
			song[i] = Integer.parseInt(qqHexStr.substring(i * 2, i * 2 + 2), 16);

		int t1 = 0;
		int t2 = 0;
		int t3 = 0;
		t1 = (lrcId & 0xff00) >> 8;
		if ((lrcId & 0xff0000) == 0)
			t3 = 0xff & ~t1;
		else
			t3 = 0xff & (lrcId & 0xff0000) >> 16;
		t3 |= (0xff & lrcId) << 8;
		t3 <<= 8;
		t3 |= 0xff & t1;
		t3 <<= 8;
		if ((lrcId & 0xff000000) == 0)
			t3 |= 0xff & ~lrcId;
		else
			t3 |= 0xff & lrcId >> 24;
		int j;
		for (j = length - 1; j >= 0; j--)
		{
			int c = song[j];
			if (c >= 128)
				c -= 256;
			t1 = (int)((long)(c + t2) & 0xffffffffL);
			t2 = (int)((long)(t2 << j % 2 + 4) & 0xffffffffL);
			t2 = (int)((long)(t1 + t2) & 0xffffffffL);
		}

		j = 0;
		t1 = 0;
		for (; j <= length - 1; j++)
		{
			int c = song[j];
			if (c >= 128)
				c -= 256;
			int t4 = (int)((long)(c + t1) & 0xffffffffL);
			t1 = (int)((long)(t1 << j % 2 + 3) & 0xffffffffL);
			t1 = (int)((long)(t1 + t4) & 0xffffffffL);
		}

		int t5 = (int)Conv(t2 ^ t3);
		t5 = (int)Conv(t5 + (t1 | lrcId));
		t5 = (int)Conv(t5 * (t1 | t3));
		t5 = (int)Conv(t5 * (t2 ^ lrcId));
		long t6 = t5;
		if (t6 > 0x80000000L)
			t5 = (int)(t6 - 0x100000000L);
		return Integer.toString(t5);
	}

	public static long Conv(int i)
	{
		long r = (long)i % 0x100000000L;
		if (i >= 0 && r > 0x80000000L)
			r -= 0x100000000L;
		if (i < 0 && r < 0x80000000L)
			r += 0x100000000L;
		return r;
	}

	public static String md5(String s)
	{
		if (s == null || s.equals(""))
			return md5("_123456_");
		String s1 = "";
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(s.getBytes("UTF-8"));
			byte abyte0[] = md.digest();
			s1 = byte2hex(abyte0);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return s1;
	}

	private static String byte2hex(byte abyte0[])
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < abyte0.length; i++)
		{
			int j = abyte0[i] & 0xff;
			if (j < 16)
				sb.append("0");
			sb.append(Integer.toString(j, 16).toLowerCase(Locale.getDefault()));
		}

		return sb.toString();
	}

	public static boolean isEmpty(String content)
	{
		if (content == null)
			return true;
		return content.trim().length() == 0;
	}

	public static void main(String args[]) throws Exception {
		List<QianQianJingTingLyricInfo> list = search("侃侃", "滴答");
		if (list.size() > 0){		
			QianQianJingTingLyricInfo result = (QianQianJingTingLyricInfo)list.get(0);
			System.out.println(fetchLyricContent(result.getLrcId(), result.getLrcCode()));
		}
	}
}