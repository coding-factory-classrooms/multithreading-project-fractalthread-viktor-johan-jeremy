package org.example;

import org.example.core.Conf;
import org.example.core.Template;
import org.example.middlewares.LoggerMiddleware;
import spark.Spark;

import java.util.HashMap;

public class App {

    public static void main(String[] args) {
        initialize();

        Spark.get("/", (req, res) -> {
            FractalExplorer fractalExplorer = new FractalExplorer(-3.0,3.0,100.0);
            fractalExplorer.updateFractal();
            HashMap<String, Object> model = new HashMap<>();
            model.put("mandelbrot", fractalExplorer);
            model.put("path", "/img/mandelbrot.png");
            return Template.render("home.html", model);
        });

        Spark.get("/fractal/:zoomFactor/:x/:y/:action/:topx/:topy", (req, res) ->{
            double zoomFactor = Double.parseDouble(req.params("zoomFactor"));
            double x =  Double.parseDouble(req.params("x"));
            double y = Double.parseDouble(req.params("y"));
            int action = Integer.parseInt(req.params("action"));

            double topx = Double.parseDouble(req.params("topx"));
            double topy = Double.parseDouble(req.params("topy"));

            FractalExplorer fractalExplorer = new FractalExplorer(topx,topy,zoomFactor);
            fractalExplorer.requestPictureGeneration(zoomFactor,x,y,action);

            HashMap<String, Object> model = new HashMap<>();
            model.put("mandelbrot", fractalExplorer);
            model.put("path", "../../../../../../img/mandelbrot.png");

            return Template.render("home.html", model);
        });
    }

    static void initialize() {
        Template.initialize();

        // Display exceptions in logs
        Spark.exception(Exception.class, (e, req, res) -> e.printStackTrace());

        // Serve static files (img/css/js)
        Spark.staticFiles.externalLocation(Conf.STATIC_DIR.getPath());

        // Configure server port
        Spark.port(Conf.HTTP_PORT);
        final LoggerMiddleware log = new LoggerMiddleware();
        Spark.before(log::process);
    }
}
