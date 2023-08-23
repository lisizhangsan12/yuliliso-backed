package com.yupi.springbootinit;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class CrawlerTest {
    @Autowired
    private PostService postService;
    @Test
    void testFetchPicture() throws IOException {
        int current = 1;
        List<Picture> pictures = new ArrayList<>();
        String url = String.format("https://cn.bing.com/images/search?q=小黑子&form=IQFRBA&id=77" +
                "B5251F2BB54280F4E1360130B1FECD4CF816E7&first=%s",current) ;
        Document doc = Jsoup.connect(url).get();
        System.out.println(doc);
        Elements elements = doc.select(".iuscp.isv");
       /* log(doc.title());
        Elements newsHeadlines = doc.select("#mp-itn b a");*/
        for (Element element : elements) {
            String m = element.select(".iusc").get(0).attr("m");
            String title = element.select(".inflnk").get(0).attr("aria-label");
            Map<String,Object> map = JSONUtil.toBean(m, Map.class);
            String  murl =(String) map.get("murl");
            Picture picture = new Picture();
            picture.setTitle(title);
            picture.setUrl(murl);
            pictures.add(picture);
        }
    }
    @Test
    void testFetchPassage() {
        //1.获取数据
        String json = "{\n" +
                "    \"current\": 1,\n" +
                "    \"pageSize\": 8,\n" +
                "    \"sortField\": \"createTime\",\n" +
                "    \"sortOrder\": \"descend\",\n" +
                "    \"category\": \"文章\",\n" +
                "    \"reviewStatus\": 1\n" +
                "}";
        String result2 = HttpRequest.post("\n" +
                "https://www.code-nav.cn/api/post/search/page/vo")
                .body(json)
                .execute()
                .body();
        System.out.println(result2);
        //2.从json转对象
        Map<String, Object> map = JSONUtil.toBean(result2, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Post> postList = new ArrayList<>();
        for (Object record : records) {
            JSONObject tempRecord = (JSONObject) record;
            Post post = new Post();
            post.setTitle(tempRecord.getStr("title"));
            post.setContent(tempRecord.getStr("content"));
            JSONArray tags = (JSONArray) tempRecord.get("tags");
            List<String> tagList = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tagList));
            post.setUserId(1L);
            postList.add(post);
        }
//        System.out.println(postList);
        // 3. 数据入库
        boolean b = postService.saveBatch(postList);
        Assertions.assertTrue(b);
    }
}
