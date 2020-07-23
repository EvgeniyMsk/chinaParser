package com.company;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ProductParser {
    private static String URL;
    private static Document document;
    private static String id;
    String path;

    public ProductParser(String newpath, String newUrl) {
        URL = newUrl;
        path = newpath;
        try {
            document = Jsoup.connect(URL).userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
            id = document.select("meta").get(6).attr("content");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProductName() {
        Elements elements = document.select("#mod-detail-title > h1");
        return (elements.text());
    }

    public String getProductAmount()
    {
        Elements elements = document.select("#mod-detail-price > div > table > tbody > tr.amount");
        return (elements.text());
    }

    public String getProductPrice() {
        Elements elements = document.select("#mod-detail-bd > div.region-custom.region-detail-property.region-takla.ui-sortable.region-vertical > div.widget-custom.offerdetail_huopin_base_flashpriceInfo > div > div > div.d-content");
        try {
            elements.addAll(document.select("#mod-detail-price > div > table > tbody > tr.price"));
        }
        catch (NullPointerException e)
        {

        }
        return (elements.text());
    }

    public String[] getProductDescription() throws IOException {
        String link = document.getElementsByClass("desc-lazyload-container").attr("data-tfs-url");
        Document document1 = Jsoup.connect(link)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .maxBodySize(0)
                .get();
        Elements elements = document1.select("span");
        String[] strings = new String[elements.size()];
        int i = 0;
        for (Element element : elements) {
            strings[i] = element.getElementsByAttributeValueNot("tag", "strong").text();
            i++;
        }
        return (strings);
    }

    public String[] getProductDescriptionImages() throws IOException {
        String link = document.getElementsByClass("desc-lazyload-container").attr("data-tfs-url");
        Document document1 = Jsoup.connect(link)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .maxBodySize(0)
                .get();
        Elements elements = document1.select("img");
        String[] strings = new String[elements.size()];
        int i = 0;
        for (Element element : elements) {
            strings[i] = correctLink(element.attr("src"));
            i++;
        }
        return (strings);
    }

    public String[][] getPictures() {
        Elements elements = document.select("#mod-detail-bd > div.region-custom.region-detail-gallery.region-takla.ui-sortable.region-vertical > div > div > div").select("ul").select("li");
        int i = 0;
        String[][] strings = new String[elements.size()][2];
        for (Element element : elements) {
            strings[i][0] = correctLink(element.attr("data-imgs").substring(12, 94));
            strings[i][1] = correctLink(element.attr("data-imgs").substring(100));
            i++;
        }
        return (strings);
    }

    public void saveProductInfo() throws IOException{
        int i;
        PrintWriter printWriter;
        // Создание папки для товара (главная папка)
        File mainDir = new File(path + id);
        if (!mainDir.exists())
        {
            mainDir.mkdir();
            // Создание папки для товара (создание папки desc_images)
            new File(path + id + "/" + "desc_images").mkdir();
            // Создание папки для товара (создание папки main_page_images)
            new File(path + id + "/" + "main_page_images").mkdir();

            // Сохранение названия товара
            printWriter = new PrintWriter(new File(path + "/" + id + "/" + "name.txt"));
            printWriter.println(getProductName());
            printWriter.close();

            // Сохранение количества и цен товара
            printWriter = new PrintWriter(new File(path + "/" + id + "/" + "price.txt"));
            printWriter.println(getProductAmount());
            printWriter.println(getProductPrice());
            printWriter.close();

            // Сохранение описания товара
            printWriter = new PrintWriter(new File(path + "/" + id + "/" + "description.txt"));
            for (String string : getProductDescription())
                printWriter.println(string);
            printWriter.close();

            // Сохранение картинок desc_images
            i = 0;
            for (String[] link : getPictures())
            {
                File out1 = new File(path + id + "/" + "desc_images" + "/" + id + "_" + i + "_mini.jpg");
                new Thread(new ProductDownloader(link[0], out1)).start();
                File out2 = new File(path + id + "/" + "desc_images" + "/" + id + "_" + i + ".jpg");
                new Thread(new ProductDownloader(link[1], out2)).start();
                i++;
            }

            // Сохранение картинок main_page_images
            i = 0;
            for (String[] link : getPictures())
            {
                File out = new File(path + id + "/" + "main_page_images" + "/" + id + "_" + i + ".jpg");
                new Thread(new ProductDownloader(link[0], out)).start();
                i++;
            }
            doDownload();
        }

        else
            System.out.println("Продукт " + id + " был добавлен ранее!");
    }

    public String correctLink(String link)
    {
        int start = link.indexOf("https");
        int finish = link.indexOf("jpg");
        String correct = link.substring(start, finish + 3);
        return (correct);
    }

    public void doDownload() throws IOException
    {
        System.out.println("Название товара: \t" + getProductName());
        System.out.println("Количество товаров: \t" + getProductAmount());
        System.out.println("Цена: \t\t\t" + getProductPrice());
        System.out.println("Описание: ");
        for (String string : getProductDescription())
            System.out.println(string);
        System.out.println("_______________________________________________________________________________");
        System.out.println("Картинки к описанию: ");
        for (String string : getProductDescriptionImages())
            System.out.println(string);
        System.out.println("_______________________________________________________________________________");
        System.out.println("Картинки на основную страницу товара:");
        for (String[] string : getPictures())
        {
            System.out.println(string[0]);
            System.out.println(string[1]);
        }
        System.out.println("Загрузка завершена!");
    }
}