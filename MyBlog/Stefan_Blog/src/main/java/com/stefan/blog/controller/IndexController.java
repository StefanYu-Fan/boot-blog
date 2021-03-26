package com.stefan.blog.controller;

import com.stefan.blog.model.Comment;
import com.stefan.blog.model.Upvote;
import com.stefan.blog.model.User;
import com.stefan.blog.model.UserContent;
import com.stefan.blog.service.*;
import com.stefan.blog.utils.DateUtils;
import com.stefan.blog.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.stefan.blog.utils.PageHelper.Page;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController extends BaseController{

    @RequestMapping("index")
    public String index(){
        return "index";
    }

    @RequestMapping("/detail")
    public String detail(){
        return "other/home";
    }

    @RequestMapping("/about")
    public String about(){
        return "about";
    }

    @RequestMapping("/article")
    public String article(){
        return "article";
    }

    @RequestMapping("/resource")
    public String resource(){
        return "resource";
    }

    @RequestMapping("/timeline")
    public String timeline(){
        return "timeline";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/register")
    public String regist(){
        return "regist";
    }

//    @RequestMapping("/activeSuccess")
//    public String activeSuccess(){
//        return "active-regist/activeSuccess";
//    }
//
//    @RequestMapping("/activeFail")
//    public String activeFail(){
//        return "active-regist/activeFail";
//    }
//
//    @RequestMapping("/registSuccess")
//    public String registSuccess(){
//        return "active-regist/registSuccess";
//    }

//    @RequestMapping("/personal")
//    public String personal(){
//        return "userinfo/personal";
//    }

//    @RequestMapping("/profile")
//    public String profile(){
//        return "userinfo/profile";
//    }

//    @RequestMapping("/repassword")
//    public String repassword(){
//        return "userinfo/repassword";
//    }
//
//    @RequestMapping("/passwordSuccess")
//    public String passwordSucc(){
//        return "userinfo/passwordSuccess";
//    }
//
//    @RequestMapping("/watch")
//    public String watch(){
//        return "userinfo/watch";
//    }
//
//    @RequestMapping("/markdown")
//    public String markdown(){
//        return "writing/markdown";
//    }
//
//    @RequestMapping("/writeblog")
//    public String write(){
//        return "writing/writeblog";
//    }
//
//    @RequestMapping("/writeSuccess")
//    public String writeSuccess(){
//        return "writing/writeSuccess";
//    }
//
//    @RequestMapping("/404")
//    public String error404(){
//        return "errorpage/404";
//    }
//
//    @RequestMapping("/error500")
//    public String error500(){
//        return "errorpage/500";
//    }

    @Autowired
    private UserContentService userContentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private UpvoteService upvoteService;

    @Autowired
    private SolrService solrService;

    private final static Logger log = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/index_list")
    public String findAllList(Model model, @RequestParam(value = "keyword",required = false) String keyword ,
                              @RequestParam(value = "pageNum",required = false) Integer pageNum ,
                              @RequestParam(value = "pageSize",required = false) Integer pageSize) {

        log.info( "===========进入index_list=========" );
        User user = (User)getSession().getAttribute("user");
        if(user!=null){
            model.addAttribute( "user",user );
        }
        if(StringUtils.isNotBlank(keyword)){
            Page<UserContent> page = solrService.findByKeyWords( keyword ,pageNum,pageSize);
            model.addAttribute("keyword", keyword);
            model.addAttribute("page", page);
        }else {
            Page<UserContent> page = findAll(pageNum, pageSize);
            model.addAttribute("page", page);
        }
        return "index";
    }

    /**
     * 点赞或踩
     * @param model
     * @param id
     * @param uid
     * @param upvote
     * @return
     */
    @RequestMapping("/upvote")
    @ResponseBody
    public Map<String,Object> upvote(Model model, @RequestParam(value = "id",required = false) long id,
                                     @RequestParam(value = "uid",required = false) Long uid,
                                     @RequestParam(value = "upvote",required = false) int upvote) {
        log.info( "id="+id+",uid="+uid+"upvote="+upvote );
        Map map = new HashMap<String,Object>(  );
        //先用session获取，后去增加security
        User user = (User)getSession().getAttribute("user");


        if(user == null){
            map.put( "data","fail" );
            return map;
        }
        Upvote up = new Upvote();
        up.setContentId( id );
        up.setuId( user.getId() );
        Upvote upv = upvoteService.findByUidAndConId( up );
        if(upv!=null){
            log.info( upv.toString()+"============" );
        }
        UserContent userContent =   userContentService.findById( id );
        if(upvote == -1){
            if(upv != null ){
                if( "1".equals( upv.getDownvote() ) ){
                    map.put( "data","down" );
                    return map;
                }else {
                    upv.setDownvote( "1" );
                    upv.setUpvoteTime( new Date(  ) );
                    upv.setIp( getClientIpAddress() );
                    upvoteService.update(upv);
                }

            }else {
                up.setDownvote( "1" );
                up.setUpvoteTime( new Date(  ) );
                up.setIp( getClientIpAddress() );
                upvoteService.add(up);
            }

            userContent.setDownvote( userContent.getDownvote()+upvote);
        }else {
            if(upv != null){
                if( "1".equals( upv.getUpvote() ) ){
                    map.put( "data","done" );
                    return map;
                }else {
                    upv.setUpvote( "1" );
                    upv.setUpvoteTime( new Date(  ) );
                    upv.setIp( getClientIpAddress() );
                    upvoteService.update(upv);
                }

            }else {
                up.setUpvote( "1" );
                up.setUpvoteTime( new Date(  ) );
                up.setIp( getClientIpAddress() );
                upvoteService.add(up);
            }


            userContent.setUpvote( userContent.getUpvote() + upvote );
        }
        userContentService.updateById( userContent );
        map.put( "data","success" );
        return map;

    }

    /**
     * 点击展开列表
     * @param model
     * @param content_id
     * @return
     */
    @RequestMapping("/reply")
    @ResponseBody
    public Map<String,Object> reply(Model model, @RequestParam(value = "content_id",required = false) Long content_id) {
        Map map = new HashMap<String,Object>(  );
        List<Comment> list = commentService.findAllFirstComment(content_id);
        if(list!=null && list.size()>0){
            for(Comment c:list){
                List<Comment> coments = commentService.findAllChildrenComment( c.getConId(), c.getChildren() );
                if(coments!=null && coments.size()>0){
                    for(Comment com:coments){
                        if(com.getById()!=null ){
                            User byUser = userService.findById( com.getById() );
                            com.setByUser( byUser );
                        }

                    }
                }
                c.setComList( coments );
            }
        }

        map.put( "list",list );

        return map;

    }

    /**
     * 点击评论按钮
     * @param model
     * @param id
     * @param content_id
     * @param uid
     * @param bid
     * @param oSize
     * @param comment_time
     * @param upvote
     * @return
     */
    @RequestMapping("/comment")
    @ResponseBody
    public Map<String,Object> comment(Model model, @RequestParam(value = "id",required = false) Long id ,
                                      @RequestParam(value = "content_id",required = false) Long content_id ,
                                      @RequestParam(value = "uid",required = false) Long uid ,
                                      @RequestParam(value = "by_id",required = false) Long bid ,
                                      @RequestParam(value = "oSize",required = false) String oSize,
                                      @RequestParam(value = "comment_time",required = false) String comment_time,
                                      @RequestParam(value = "upvote",required = false) Integer upvote) {
        Map map = new HashMap<String,Object>(  );
        User user = (User)getSession().getAttribute("user");
        if(user == null){
            map.put( "data","fail" );
            return map;
        }
        if(id==null ){
            Date date = DateUtils.StringToDate( comment_time, "yyyy-MM-dd HH:mm:ss" );

            Comment comment = new Comment();
            comment.setComContent( oSize );
            comment.setCommTime( date );
            comment.setConId( content_id );
            comment.setComId( uid );
            if(upvote==null){
                upvote = 0;
            }
            comment.setById( bid );
            comment.setUpvote( upvote );
            User u = userService.findById( uid );
            comment.setUser( u );
            commentService.add( comment );
            map.put( "data",comment );
            UserContent userContent = userContentService.findById( content_id );
            Integer num = userContent.getCommentNum();
            userContent.setCommentNum( num+1 );
            userContentService.updateById( userContent );
        }else {
            //点赞
            Comment c = commentService.findById( id );
            c.setUpvote( upvote );
            commentService.update( c );
        }
        return map;
    }


    /**
     * 删除评论
     * @param model
     * @param id
     * @param uid
     * @param con_id
     * @param fid
     * @return
     */
    @RequestMapping("/deleteComment")
    @ResponseBody
    public Map<String,Object>  deleteComment(Model model, @RequestParam(value = "id",required = false) Long id,@RequestParam(value = "uid",required = false) Long uid,
                                             @RequestParam(value = "con_id",required = false) Long con_id,@RequestParam(value = "fid",required = false) Long fid) {
        int num = 0;
        Map map = new HashMap<String,Object>(  );
        User user = (User) getSession().getAttribute("user");
        if(user==null){
            map.put( "data","fail" );
        }else{
            if(user.getId().equals( uid )){
                Comment comment = commentService.findById( id );
                if(StringUtils.isBlank( comment.getChildren() )){
                    if(fid!=null){
                        //去除id
                        Comment fcomm = commentService.findById( fid );
                        String child = StringUtil.getString( fcomm.getChildren(), id );
                        fcomm.setChildren( child );
                        commentService.update( fcomm );
                    }
                    commentService.deleteById(id);
                    num = num + 1;
                }else {
                    String children = comment.getChildren();
                    commentService.deleteChildrenComment(children);
                    String[] arr = children.split( "," );
                    commentService.deleteById( id );
                    num = num + arr.length + 1;
                }
                UserContent content = userContentService.findById( con_id );
                if(content!=null){
                    if(content.getCommentNum() - num >= 0){
                        content.setCommentNum( content.getCommentNum() - num );
                    }else {
                        content.setCommentNum( 0 );
                    }
                    userContentService.updateById( content );
                }
                map.put( "data",content.getCommentNum() );
            }else {
                map.put( "data","no-access" );
            }
        }
        return  map;
    }

    /**
     * 点击一级评论块的评论按钮
     * @param model
     * @param id
     * @param content_id
     * @param uid
     * @param bid
     * @param oSize
     * @param comment_time
     * @param upvote
     * @return
     */
    @RequestMapping("/comment_child")
    @ResponseBody
    public Map<String,Object> addCommentChild(Model model, @RequestParam(value = "id",required = false) Long id ,
                                              @RequestParam(value = "content_id",required = false) Long content_id ,
                                              @RequestParam(value = "uid",required = false) Long uid ,
                                              @RequestParam(value = "by_id",required = false) Long bid ,
                                              @RequestParam(value = "oSize",required = false) String oSize,
                                              @RequestParam(value = "comment_time",required = false) String comment_time,
                                              @RequestParam(value = "upvote",required = false) Integer upvote) {
        Map map = new HashMap<String,Object>(  );
        User user = (User) getSession().getAttribute("user");
        if(user == null){
            map.put( "data","fail" );
            return map;
        }

        Date date = DateUtils.StringToDate( comment_time, "yyyy-MM-dd HH:mm:ss" );

        Comment comment = new Comment();
        comment.setComContent( oSize );
        comment.setCommTime( date );
        comment.setConId( content_id );
        comment.setComId( uid );
        if(upvote==null){
            upvote = 0;
        }
        comment.setById( bid );
        comment.setUpvote( upvote );
        User u = userService.findById( uid );
        comment.setUser( u );
        commentService.add( comment );

        Comment com = commentService.findById( id );
        if(StringUtils.isBlank( com.getChildren() )){
            com.setChildren( comment.getId().toString() );
        }else {
            com.setChildren( com.getChildren()+","+comment.getId() );
        }
        commentService.update( com );
        map.put( "data",comment );

        UserContent userContent = userContentService.findById( content_id );
        Integer num = userContent.getCommentNum();
        userContent.setCommentNum( num+1 );
        userContentService.updateById( userContent );
        return map;

    }

}
