package org.example;

import org.example.core.Conf;
import org.example.core.Template;
import org.example.middlewares.LoggerMiddleware;
import spark.Spark;
import spark.utils.IOUtils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

public class App {

    public static void main(String[] args) {
        initialize();

        LRUCache<String, File> cache = new LRUCache<>(5);
        final String cachePath = "org/example/cachePictures";

        //utiliser pour aller chercher la taille de la fenêtre du navigateur
        Spark.get("/", (req, res) -> Template.render("start.html", new HashMap<>()));

        //quand on clique pour zoomer / dézoomer
        Spark.get("/fractal/:zoomFactor/:x/:y/:action/:topx/:topy/:width/:height", (req, res) ->{
            String racine = "../../../../../../../../";

            double zoomFactor = Double.parseDouble(req.params("zoomFactor"));
            double x =  Double.parseDouble(req.params("x"));
            double y = Double.parseDouble(req.params("y"));
            int action = Integer.parseInt(req.params("action"));

            double topx = Double.parseDouble(req.params("topx"));
            double topy = Double.parseDouble(req.params("topy"));

            int width = Integer.parseInt(req.params("width"));
            System.out.println("width = " + width);

            int height = Integer.parseInt(req.params("height"));
            System.out.println("height = " + height);

            HashMap<String, Object> model = new HashMap<>();
            FractalExplorer fractalExplorer = new FractalExplorer(topx,topy,zoomFactor,width,height);

            fractalExplorer.requestZoomPicture(x,y,action);
            File imageInCache = cache.get(fractalExplorer.provideKey());
            if(imageInCache != null){

                System.out.println("image found in cache !");
            } else {
                System.out.println("image not found in cache !");
                fractalExplorer.updateFractal();
               // cache.put(fractalExplorer.provideKey(), x);
                //         ../ pour chaque param dans l'adresse
                //model.put("path", "../../../../../../../../../../org/example/cachePictures" + );
            }
            model.put("mandelbrot", fractalExplorer);

            return Template.render("home.html", model);
        });

        //après avoir récupéré le width et le height de la fenêtre du naviateur
        Spark.get("/fractal/:w/:h", (req, res) ->{
            FractalExplorer fractalExplorer = new FractalExplorer(
                    -(Double.parseDouble(req.params("w"))/175),
                    Double.parseDouble(req.params("h"))/185,
                    100.0,
                    Integer.parseInt(req.params("w")),
                    Integer.parseInt(req.params("h")
                    ));
            fractalExplorer.updateFractal();

            HashMap<String, Object> model = new HashMap<>();
            model.put("mandelbrot", fractalExplorer);
            //      ../ pour chaque param dans l'adresse
            model.put("path", "../../img/mandelbrot.png");

            return Template.render("home.html", model);
        });

        //dans le cas où l'on se déplace avec zqsd
        Spark.get("/fractal/:type/:zoom/:topx/:topy/:width/:height", (req, res) ->{
            String racinePath = "../../../../../../../../../";
            String imgPath = "resources/static/img/";

            FractalExplorer fractalExplorer = new FractalExplorer(
                    Double.parseDouble(req.params("topx")),
                    Double.parseDouble(req.params("topy")),
                    Double.parseDouble(req.params("zoom")),
                    Integer.parseInt(req.params("width")),
                    Integer.parseInt(req.params("height"))
            );

            fractalExplorer.requestMovePicture(req.params("type"));
            File imageInCache = new File(racinePath+cachePath+fractalExplorer.provideKey()+".png");
            if(imageInCache.exists()){
                System.out.println("image found in cache !");
                InputStream input = new FileInputStream(racinePath+cachePath+fractalExplorer.provideKey()+".png");
                OutputStream output = new FileOutputStream(racinePath+imgPath);
                IOUtils.copy(input, output);
            } else {
                System.out.println("image not found in cache !");
                fractalExplorer.updateFractal();
                InputStream input = new FileInputStream("img/mandelbrot.png");
                OutputStream output = new FileOutputStream(racinePath+cachePath+fractalExplorer.provideKey()+".png");
                IOUtils.copy(input, output);
                cache.put(fractalExplorer.provideKey(), new File(racinePath+cachePath+fractalExplorer.provideKey()+".png"));
            }

            HashMap<String, Object> model = new HashMap<>();
            model.put("mandelbrot", fractalExplorer);
            //      ../ pour chaque param dans l'adresse
            model.put("path", racinePath+imgPath+"mandelbrot.png");

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
