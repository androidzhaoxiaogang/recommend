package DianYingTianTang;

import java.util.HashMap;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import file.getContentFromUrl;

public class GetMovieByUrl implements Runnable{
	
	private String url;
	private int type;
	
	public String getUrl() {
		return url;
	}
	
	public GetMovieByUrl(String _url, int type) {
		this.url = _url;
		this.type = type;
	}
	
	private void newMovie() {
		System.out.println(url);
		Vector <MovieTianTang> MovieUrlSet;
		MovieUrlSet = new Vector<MovieTianTang>();
		HashMap <String, String> q = new HashMap<String, String>();
		String urlContent = getContentFromUrl.myGetContentFromUrl(url, "gb2312");
		Document curDoc = Jsoup.parse(urlContent);
		Element allMovie = curDoc.getElementsByClass("co_content8").get(0);
		Elements eleMovie = allMovie.getElementsByTag("table");
		
		for (int j = 0; j < eleMovie.size(); j++) {
			Element now = eleMovie.get(j).getElementsByAttribute("href").get(1);//1
			String MovieUrl = "http://www.ygdy8.net" + now.attr("href");
			String MovieName = eleMovie.get(j).getElementsByClass("ulink").get(1).text();//1
			
			System.out.println(MovieName);
			if (MovieName.indexOf('《') + 1 > MovieName.lastIndexOf('》'))
				continue;
			String trueName = MovieName.substring(MovieName.indexOf('《') + 1, MovieName.lastIndexOf('》'));
			trueName = trueName.replace('/', '.');
			if (type == 1 && ChinaMovie.isExist(trueName)) {
				continue;
			}
			if (type == 2 && OuMeiMovie.isExist(trueName)) {
				continue;
			}
			if (type == 3 && RiHanMovie.isExist(trueName)) {
				continue;
			}
			if (q.get("MovieUrl") != "true") {
				q.put(MovieUrl, "true");
				MovieTianTang movie = new MovieTianTang();
				movie.setName(trueName);
				movie.setUrlOpen(MovieUrl);
				MovieUrlSet.addElement(movie);
			}
		}
		
		for (int i = 0; i < MovieUrlSet.size(); i++) {
			//获取海报和电影图
			String nowUrl = MovieUrlSet.get(i).getUrlOpen();
			String nowName = MovieUrlSet.get(i).getName();
			String nowUrlContent = getContentFromUrl.myGetContentFromUrl(nowUrl, "gb2312");
			Document curUrlDoc = Jsoup.parse(nowUrlContent);
			Element nowMoviePage = curUrlDoc.getElementById("Zoom");
			if (nowMoviePage == null)
				continue;
			Element photo = nowMoviePage.getElementsByTag("span").get(0);
			Elements image = photo.getElementsByTag("img");
			
			String imgPoster = null;
			if (image.size() > 0 && image.get(0).attr("src") != null)
				imgPoster = image.get(0).attr("src");
			
			String imgScrene = null;
			if (image.size() > 1 && image.get(1).attr("src") != null)
				imgScrene = image.get(1).attr("src");
			// 获取下载地址
			Element downLoad = photo.getElementsByTag("table").get(0);
			String downLoadAdd = downLoad.getElementsByTag("a").get(0).attr("href");
			
			MovieUrlSet.get(i).setPoster(imgPoster);
			MovieUrlSet.get(i).setScreen(imgScrene);
			MovieUrlSet.get(i).setDownLoadUrl(downLoadAdd);
		}
		if (type == 1)
			ChinaMovie.add(MovieUrlSet);
		if (type == 2)
			OuMeiMovie.add(MovieUrlSet);
		if (type == 3)
			RiHanMovie.add(MovieUrlSet);
	}

	@Override
	public void run() {
		newMovie();
	}


}
