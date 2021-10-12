package com.rbm.web.prepago.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rbm.web.prepago.util.ConstantesWebPrepago;

@WebServlet("/images/*")
public class ImageServlet extends HttpServlet {


    /**
	 * 
	 */
	private static final long serialVersionUID = 3349735731068466147L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getPathInfo()!=null){
		    String filename = request.getPathInfo().substring(1);
		    File file = new File(ConstantesWebPrepago.IMAGENES_DIR, filename);
		    response.setHeader("Content-Type", getServletContext().getMimeType(filename));
		    response.setHeader("Content-Length", String.valueOf(file.length()));
		    response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
		    OutputStream outStream = response.getOutputStream();
		   if  (!file.exists() || !file.canRead()) {
			     outStream.write("<i>Imagen no encontrada</i>".getBytes());
		   } else {
			   FileInputStream inStream = new FileInputStream(file);
			   byte[] buffer = new byte[1024];
			   while (true) {
				   int bytes = inStream.read(buffer);
				   if (bytes <= 0) {
					   break;
				   }
				   outStream.write(buffer, 0, bytes);
			   }
			   inStream.close();
		   }
		   outStream.flush();
	    }
	}

}