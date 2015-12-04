package com.example.shareholders.config;

import android.os.Environment;

/**
 * 
 * @author warren
 * @version 1.0
 * @date 2015-1-31
 */
public class AppConfig {
	/** 默认 SharePreferences文件名. */
	public final static String SHARED_PATH = "app_share";
	public final static String APP_NAME_STRING = "ShareHolders";
	// 当前版本url
	public final static String VERSION_URL = "http://120.24.254.176:8080/shareholder-server/api/v1.0/";
	// 账户接口URL前缀
	public final static String URL_ACCOUNT = VERSION_URL + "account/";
	// 基金接口URL前缀
	public final static String URL_FUND = VERSION_URL + "fund/";
	// 咨询接口URL前缀
	public final static String URL_INFO = VERSION_URL + "info/";
	// 新闻接口URL前缀
	public final static String URL_NEWS = VERSION_URL + "news/";
	// 用户接口URL前缀
	public final static String URL_USER = VERSION_URL + "user/";
	// 上传文件接口URL前缀
	public final static String URL_FILE = VERSION_URL + "file/";
	// 调研接口URL前缀
	public final static String URL_SURVEY = VERSION_URL + "survey/";
	// 话题接口URL前缀
	public final static String URL_TOPIC = VERSION_URL + "topic/";
	// 信息接口URL前缀
	public final static String URL_MESSAGE = VERSION_URL + "message/";
	// 第三方前缀
	public final static String URL_THIRD = VERSION_URL + "third/";
	// quotation
	public final static String URL_QUOTATION = VERSION_URL + "quotation/";
	// 腾讯app id
	public final static String TENCENT_APP_ID = "1104754441";
	// 微博app key
	public final static String WEIBO_APP_KEY = "2862061440";
	public static final String REDIRECT_URL = "http://open.weibo.com";
	// 微信app id
	public final static String WEIXIN_APP_ID = "wxa5bba648589e48a4";
	// 商城host
	public static final String HOST_SHOP = "192.168.31.143:8080";
	// 商城url
	public static final String URL_SHOP = "http://120.24.168.153:8080/shop/user/";
	//获取用户权限
	public static final String SURVEY_RIGHT = "http://120.24.254.176:8080/shareholder-server/api/v1.0/user/";

	
	/**
	 * WeiboSDKDemo 应用对应的权限，第三方开发者一般不需要这么多，可直接设置成空即可。 详情请查看 Demo 中对应的注释。
	 */
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";
	// 用户头像
	public static final String FIGURE_URL = "figureurl";
	// 用户名
	public static final String NICKNAME = "nickname";
	// 一句话介绍
	public static final String INTRODUCE_CONTENT = "introduce_content";
	// 行业
	public static final String INDUSTRY = "industry";
	// 地址
	public static final String LOCATION = "city_location";
	// 电话号码
	public static final String PHONE_NUMBER = "phone_number";
	// 验证码
	public static final String VERIFY_CODE = "verify_code";
	// access_token
	public static final String ACCESS_TOKEN = "access_token";
	// 录音文件的路径
	public static final String AUDIO_PATH = Environment
			.getExternalStorageDirectory() + "/shareholder_audio";
	// 测试用custId
	public static final String custId = "1";
	// 第三方注册凭证
	public static final String THIRD_ACCESS_TOKEN = "accessToken";
	// 第三方注册类型
	public static final String THIRD_TYPE = "type";
	// 是否是通过第三方注册
	public static final String IS_FORM_THIRD = "is_from_third";
	// 微信openId
	public static final String OPENID = "openid";
	// 微信AppSecret
	public static final String AppSecret = "99b3d87eae301f279355d1b170f525be";
	// 测试用custId
	public static final String custUuid = "EB871F93FD5E62CB49F0F6DCE959A5EE";
	// 当前用户的uuid
	public static final String UUID = "uuid";
	// 是否是第一次登录
	public static final String ISDEBUTE = "isFirstLogin";
	// 沪深的搜索的ViewPager设置为第1页
	public static final String SET_PAGE = "set_page";
	// 环信用户名
	public static final String IMUSER_NAME = "imUserName";
	// 环信密码
	public static final String IMUSER_PASSWORD = "imPassword";
	//消息置顶
	public static final String MESSAGE_TOP = "message_top";
	// 新闻详情文件的路径
	public static final String NEWS_PATH = Environment
			.getExternalStorageDirectory() + "/shareholder_news";
	public static final String ANNOUNCEMENT_PATH = Environment
			.getExternalStorageDirectory() + "/shareholder_ann";
}
