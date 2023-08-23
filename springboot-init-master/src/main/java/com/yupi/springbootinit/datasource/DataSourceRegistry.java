package com.yupi.springbootinit.datasource;

import com.yupi.springbootinit.datasource.impl.PictureDataSourceImpl;
import com.yupi.springbootinit.datasource.impl.PostDataSourceImpl;
import com.yupi.springbootinit.datasource.impl.UserDataSourceImpl;
import com.yupi.springbootinit.model.enums.SearchTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
@Component
public class DataSourceRegistry {

    @Resource
    private PostDataSourceImpl postDataSource;
    @Resource
    private PictureDataSourceImpl pictureDataSource;
    @Resource
    private UserDataSourceImpl userDataSource;
    private Map<String,DataSource> typeDataSourceMap;
    @PostConstruct
    public void init(){
        typeDataSourceMap = new HashMap(){{
            put(SearchTypeEnum.POST.getValue(),postDataSource);
            put(SearchTypeEnum.USER.getValue(),userDataSource);
            put(SearchTypeEnum.PICTURE.getValue(),pictureDataSource);
        }};
    }
    public DataSource getDataSourceByType(String type){
        if(typeDataSourceMap == null){
            return null;
        }
        return typeDataSourceMap.get(type);
    }
}
