package com.il360.shenghecar.util;

import java.net.URLEncoder;

import com.il360.shenghecar.common.GlobalPara;
import com.il360.shenghecar.model.user.ProjectileContent;
import com.il360.shenghecar.model.user.User;

/**
 * User: Wanghk
 * Date: 14-4-17
 * Time: 上午11:35
 * 用户相关辅助类
 */
public class UserUtil {
    /**
     * 用户登录后的全部信息 *
     */
    private static User userInfo;
    
    private static ProjectileContent myProjectileContent;
    
    /**
     * 是否处于登录页面 *
     */
    private static boolean mIsLoginPage;

    /**
     * 判断是否登录信息存在
     *
     * @return
     */
    public static boolean judgeUserInfo() {
        if (userInfo == null || StringUtil.isNullOrEmpty(userInfo.getToken())) {
            return false;
        }
        return true;
    }
    
    /**
     * 判断是否实名认证
     *
     * @return
     */
    public static boolean judgeAuthentication() {
        if (GlobalPara.getOutUserRz() != null && GlobalPara.getOutUserRz().getNameRz() != null
				&& GlobalPara.getOutUserRz().getNameRz() == 1) {
            return true;
        }
        return false;
    }
    
//    /**
//     * 判断是否可以贷款
//     *
//     * @return
//     */
//    public static boolean judgeCreditAmount() {
//        if (GlobalPara.getOutUserRz() != null && GlobalPara.getOutUserRz().getHbAmount() != null) {
//            return true;
//        }
//        return false;
//    }
    
    /**
     * 判断是否绑定银行卡
     *
     * @return
     */
	public static boolean judgeBankCard() {
        if (GlobalPara.getOutUserRz() != null && GlobalPara.getOutUserRz().getBankRz() != null
				&& GlobalPara.getOutUserRz().getBankRz() == 1) {
			return true;
		}
		return false;
	}
    
    /**
     * 判断是否通过手机认证
     *
     * @return
     */
	public static boolean judgeOperator() {
        if (GlobalPara.getOutUserRz() != null && GlobalPara.getOutUserRz().getPhoneRz() != null
				&& GlobalPara.getOutUserRz().getPhoneRz() == 1) {
			return true;
		}
		return false;
	}
    
//    /**
//     * 判断是否通过支付宝认证
//     *
//     * @return
//     */
//	public static boolean judgeAlipay() {
//        if (GlobalPara.getOutUserRz() != null && GlobalPara.getOutUserRz().getZfbRz() != null
//				&& GlobalPara.getOutUserRz().getZfbRz() == 1) {
//			return true;
//		}
//		return false;
//	}
//
//    /**
//     * 判断是否获取芝麻分
//     *
//     * @return
//     */
//	public static boolean judgeZhiMa() {
//        if (GlobalPara.getOutUserRz() != null && GlobalPara.getOutUserRz().getZmxyRz() != null
//				&& GlobalPara.getOutUserRz().getZmxyRz() == 1) {
//			return true;
//		}
//		return false;
//	}
	
//    /**
//     * 判断是否通过京东认证
//     *
//     * @return
//     */
//	public static boolean judgeJingDong() {
//        if (GlobalPara.getOutUserRz() != null && GlobalPara.getOutUserRz().getJdRz() != null
//				&& GlobalPara.getOutUserRz().getJdRz() == 1) {
//			return true;
//		}
//		return false;
//	}
	
    /**
     * 判断是否通过淘宝认证
     *
     * @return
     */
	public static boolean judgeTaoBao() {
        if (GlobalPara.getOutUserRz() != null && GlobalPara.getOutUserRz().getTaobaoRz() != null
				&& GlobalPara.getOutUserRz().getTaobaoRz() == 1) {
			return true;
		}
		return false;
	}

    /**
     * 配置登录信息
     *
     * @param user
     */
    public static void loadUserInfo(User user) {
        userInfo = user;
        try {
            userInfo.setToken(URLEncoder.encode(user.getToken(), "utf-8"));
        } catch (Exception e) {
            //undo
        }
    }

    /**
     * 清空用户数据
     */
    public static void clearUserInfo() {
        userInfo = null;
    }

    public static User getUserInfo() {
        return userInfo;
    }
    
    public static ProjectileContent getProjectileContent() {
        return myProjectileContent;
    }
    
    public static void setProjectileContent(ProjectileContent projectileContent) {
    	myProjectileContent = projectileContent;
    }

    public static String getToken() {
        if (userInfo != null) {
            return userInfo.getToken();
        }
        return null;
    }

//    public static String getSpecialEmail() {
//        if (userInfo == null) {
//            return "";
//        }
//        String email = userInfo.getEmail();
//        if(email==null){
//            return "";
//        }
//        int length = email.indexOf("@");
//        if (length > 3 && email.length() > 5) {
//            email = email.substring(0, length - 3) + "****" + email.substring(length - 1);
//        } else if (length > 1) {
//            email = email.substring(0, 1) + "***" + email.substring(length);
//        }
//        return email;
//    }


    public static boolean getIsLoginPage() {
        return mIsLoginPage;
    }

    public static void loadIsLoginPage(boolean isLoginPage) {
        mIsLoginPage = isLoginPage;
    }
}
