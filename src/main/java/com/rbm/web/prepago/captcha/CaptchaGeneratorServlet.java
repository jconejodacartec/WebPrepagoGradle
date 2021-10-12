package com.rbm.web.prepago.captcha;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rbm.web.prepago.managedBeans.AsignarContrasenaBean;
import com.rbm.web.prepago.managedBeans.RegistrarUsuarioBean;

import nl.captcha.Captcha;
import nl.captcha.gimpy.RippleGimpyRenderer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.servlet.SimpleCaptchaServlet;
import nl.captcha.text.renderer.DefaultWordRenderer;



public class CaptchaGeneratorServlet extends SimpleCaptchaServlet {

	private static final long serialVersionUID = 5033534081510267011L;
	private static final String textColor = "#9c9a9b";

	private List<java.awt.Color> textColors = Arrays.asList(new Color(
            	Integer.valueOf( textColor.substring( 1, 3 ), 16 ),
            	Integer.valueOf( textColor.substring( 3, 5 ), 16 ),
            	Integer.valueOf( textColor.substring( 5, 7 ), 16 ) ));
	
	private List<java.awt.Font> textFonts = Arrays.asList(
			new Font("Segoe Print", Font.PLAIN, 50));
	
	@Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Captcha captcha = new Captcha.Builder(200, 84)
			.addText(new DefaultWordRenderer(textColors, textFonts))
			.gimp(new RippleGimpyRenderer())
			.build();
		CaptchaServletUtil.writeImage(resp, captcha.getImage());
		String tipo = req.getParameter("view");
		
		if(tipo.equals("asignarPass")) {
			AsignarContrasenaBean.setCaptcha(captcha);
		}else {
			RegistrarUsuarioBean.setCaptcha(captcha);
		}
		
		req.getSession().setAttribute(Captcha.NAME, captcha);
	}

}
