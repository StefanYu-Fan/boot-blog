package com.stefan.blog.controller;

import com.stefan.blog.model.User;
import com.stefan.blog.model.UserContent;
import com.stefan.blog.service.SolrService;
import com.stefan.blog.service.UserContentService;
import com.stefan.blog.utils.ConstansUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class WriteController extends BaseController {
    private final static Logger log = LoggerFactory.getLogger(WriteController.class);
    @Autowired
    private UserContentService userContentService;

    @Autowired
    private SolrService solrService;

    /**
     * 进入writedream
     * @param model
     * @return
     */
    @RequestMapping("/writeDream")
    public String writedream(Model model,@RequestParam(value = "cid",required = false) Long cid,
                             @RequestParam(value = "style",required = false) String style) {
        User user = (User) getSession().getAttribute("user");
        if(cid!=null){
            UserContent content = userContentService.findById(cid);
            model.addAttribute("cont",content);
        }
        model.addAttribute("user", user);
        return "writing/writeblog";
    }



    /**
     * 写文章
     * @param model
     * @param id
     * @param category
     * @param txtT_itle
     * @param content
     * @param private_dream
     * @return
     */
    @RequestMapping("/doWritedream")
    public String doWritedream(Model model, @RequestParam(value = "id",required = false) String id,
                               @RequestParam(value = "cid",required = false) Long cid,
                               @RequestParam(value = "category",required = false) String category,
                               @RequestParam(value = "txtT_itle",required = false) String txtT_itle,
                               @RequestParam(value = "txt_desc",required = false) String txt_desc,
                               @RequestParam(value = "content",required = false) String content,
                               @RequestParam(value = "editormd",required = false) String editormd,
                               @RequestParam(value = "private_dream",required = false) String private_dream) {
        log.info( "进入写梦Controller" );
        User user = (User) getSession().getAttribute("user");
        UserContent userContent = new UserContent();
        if(cid!=null){
            userContent = userContentService.findById(cid);
        }
        userContent.setCategory( category );
        userContent.setContent( content );
//        userContent.setEditormd( editormd );
        userContent.setRptTime( new Date(  ) );
        String imgUrl = user.getImgUrl();
        if(StringUtils.isBlank( imgUrl )){
            userContent.setImgUrl( "/images/icon_m.jpg" );
        }else {
            userContent.setImgUrl( imgUrl );
        }
        if("on".equals( private_dream )){
            userContent.setPersonal( "1" );
        }else{
            userContent.setPersonal( "0" );
        }
        userContent.setTitle( txtT_itle );
        userContent.setContentDesc(txt_desc);
        userContent.setuId( user.getId() );
        userContent.setNickName( user.getNickName() );

        if(cid ==null){
            userContent.setUpvote( 0 );
            userContent.setDownvote( 0 );
            userContent.setCommentNum( 0 );
            userContentService.addContent( userContent );
            solrService.addUserContent(userContent);

        }else {
            userContentService.updateById(userContent);
            solrService.updateUserContent(userContent);
        }
        model.addAttribute("content",userContent);
        return "writing/writeSuccess";
    }

    /**
     * 根据文章id查看文章
     * @param model
     * @param cid
     * @return
     */
    @RequestMapping("/watch")
    public String watchContent(Model model, @RequestParam(value = "cid",required = false) Long cid){
        User user = (User)getSession().getAttribute("user");
        if(user == null){
            //未登录
            model.addAttribute( "error","请先登录！" );
            return "/login";
        }
        UserContent userContent = userContentService.findById(cid);
        model.addAttribute("cont",userContent);
        return "userinfo/watch";
    }
}
