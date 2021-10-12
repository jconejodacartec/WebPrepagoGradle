package com.rbm.web.prepago.gestores;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

public class GestorCaptcha {
	
	private static ImageCaptchaService instance = new DefaultManageableImageCaptchaService();
	 
    public static ImageCaptchaService getInstance(){
        return instance;
    }

}
