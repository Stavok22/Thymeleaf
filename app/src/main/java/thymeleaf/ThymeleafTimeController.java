package thymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet("/")
public class ThymeleafTimeController extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();

        resolver.setPrefix("D:\\HomeWork7-16\\thymeleaf\\templates\\");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("time", parseTime(req, resp)
                ));

        engine.process("time_and_date", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    private String parseTime(HttpServletRequest request, HttpServletResponse response) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");
        LocalDateTime zoneId = LocalDateTime.now(ZoneId.of("UTC"));

        if (request.getParameterMap().containsKey("timezone")) {
            String utc = request.getParameter("timezone").replace(" ", "+");
            zoneId = LocalDateTime.now(ZoneId.of(utc));

            Cookie lastTimezone = new Cookie("lastTimezone", utc);
            response.addCookie(lastTimezone);
            lastTimezone.setMaxAge(60*60);

            return dateTimeFormatter.format(zoneId) + " " + utc;
        }

        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("lastTimezone")) {
                    String cookieUtc = cookie.getValue();
                    zoneId = LocalDateTime.now(ZoneId.of(cookieUtc));
                    return dateTimeFormatter.format(zoneId) + " " + cookieUtc;
                }
            }
        }

        return dateTimeFormatter.format(zoneId) + " " + "UTC";
    }
}