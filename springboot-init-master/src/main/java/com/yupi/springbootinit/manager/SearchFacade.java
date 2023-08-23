package com.yupi.springbootinit.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.datasource.DataSource;
import com.yupi.springbootinit.datasource.DataSourceRegistry;
import com.yupi.springbootinit.datasource.impl.PictureDataSourceImpl;
import com.yupi.springbootinit.datasource.impl.PostDataSourceImpl;
import com.yupi.springbootinit.datasource.impl.UserDataSourceImpl;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.dto.search.SearchQueryRequest;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.enums.SearchTypeEnum;
import com.yupi.springbootinit.model.vo.PostVO;
import com.yupi.springbootinit.model.vo.SearchVO;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.PictureService;
import com.yupi.springbootinit.service.PostService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 搜索门面
 */
@Slf4j
@Component
public class SearchFacade {
    @Resource
    private PictureService pictureService;
    @Resource
    private DataSourceRegistry dataSourceRegistry;
    @Resource
    private PostDataSourceImpl postDataSource;
    @Resource
    private PictureDataSourceImpl pictureDataSource;
    @Resource
    private UserDataSourceImpl userDataSource;
    @Resource
    private PostService postService;

    @Resource
    private UserService userService;
    public SearchVO searchAll(@RequestBody SearchQueryRequest searchQueryRequest, HttpServletRequest request){
        String searchText = searchQueryRequest.getSearchText();
        long pageSize = searchQueryRequest.getPageSize();
        long pageNum = searchQueryRequest.getCurrent();
        String type = searchQueryRequest.getType();
        SearchTypeEnum enumByValue = SearchTypeEnum.getEnumByValue(type);
        SearchVO searchVO = new SearchVO();
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        if (enumByValue == null) {
            UserQueryRequest userQueryRequest = new UserQueryRequest();
            userQueryRequest.setUserName(searchText);
            Page<UserVO> userVOPage = userDataSource.doSearch(searchText,pageNum,pageSize);
            PostQueryRequest postRequest = new PostQueryRequest();
            postRequest.setSearchText(searchText);
            Page<PostVO> postVOPage = postDataSource.doSearch(searchText, pageNum,pageSize);
            Page<Picture> picturePage = pictureDataSource.doSearch(searchText, 1, 10);
            try {
                searchVO.setPictureList(picturePage.getRecords());
                searchVO.setUserList(userVOPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                return searchVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询异常");
            }
        }else {
            DataSource dataSourceByType = dataSourceRegistry.getDataSourceByType(type);
            //DataSource dataSource = typeDataSourceMap.get(type);
            Page page = dataSourceByType.doSearch(searchText, pageNum, pageSize);
            searchVO.setDataList(page.getRecords());
            return searchVO;
        }
    }
}
