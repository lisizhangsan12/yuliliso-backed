package com.yupi.springbootinit.datasource.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.datasource.DataSource;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.Picture;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片服务实现类
 */
@Service
public class PictureDataSourceImpl implements DataSource<Picture> {


    @Override
    public Page<Picture> doSearch(String searchText, long pageNum, long pageSize) {
        long current = (pageNum) * pageSize;
        List<Picture> pictures = new ArrayList<>();
        Document doc = null;
        String url = null;
        if("".equals(searchText) || searchText == null){
            url = String.format("https://cn.bing.com/images/search?q=null&form=IQFRBA&id=77" +
                    "B5251F2BB54280F4E1360130B1FECD4CF816E7&first=%s", current);
        }else {
            url = String.format("https://cn.bing.com/images/search?q=%s&form=IQFRBA&id=77" +
                    "B5251F2BB54280F4E1360130B1FECD4CF816E7&first=%s", searchText, current);
        }
        try{
            doc = Jsoup.connect(url).get();
        }catch (IOException e){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"数据获取异常");
        }
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
            if(pictures.size() > pageSize){
                break;
            }
        }
        Page<Picture> picturePage = new Page<>(pageNum,pageSize);
        picturePage.setRecords(pictures);
        return picturePage;

    }
}
