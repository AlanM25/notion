package upvictoria.pm_may_ago_2025.iti_271415.pi1u1.notion;

import android.content.Context;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfExporter {

    public static void exportToPdf(Context context, String title, String content) {
        if (context == null || title == null || content == null) {
            Toast.makeText(context, "Datos inválidos para exportar", Toast.LENGTH_SHORT).show();
            return;
        }

        String htmlContent = convertMarkdownToHtml(content);
        createWebViewForPdf(context, title, htmlContent);
    }

    private static void createWebViewForPdf(Context context, String title, String htmlContent) {
        WebView webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                createWebPrintJob(view, context, title);
            }
        });

        String htmlDoc = "<!DOCTYPE html>" +
                "<html><head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>" + title + "</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; padding: 20px; }" +
                "h1 { color: #2c3e50; border-bottom: 2px solid #eee; padding-bottom: 10px; }" +
                "h2 { color: #34495e; }" +
                "h3 { color: #7f8c8d; }" +
                "pre { background: #f5f5f5; padding: 10px; border-radius: 5px; overflow-x: auto; }" +
                "code { background: #f5f5f5; padding: 2px 5px; border-radius: 3px; }" +
                "strong { font-weight: bold; }" +
                "em { font-style: italic; }" +
                "</style>" +
                "</head><body>" + htmlContent + "</body></html>";

        webView.loadDataWithBaseURL(null, htmlDoc, "text/HTML", "UTF-8", null);
    }

    private static void createWebPrintJob(WebView webView, Context context, String title) {
        try {
            PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
            if (printManager == null) {
                Toast.makeText(context, "No se pudo acceder al servicio de impresión", Toast.LENGTH_SHORT).show();
                return;
            }

            String jobName = title + " Document";
            printManager.print(jobName, webView.createPrintDocumentAdapter(jobName),
                    new PrintAttributes.Builder().build());
        } catch (Exception e) {
            Toast.makeText(context, "Error al crear PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static void exportNotebooksToPdf(Context context, List<Notebook> notebooks) {
        if (notebooks == null || notebooks.isEmpty()) {
            Toast.makeText(context, "No hay cuadernos para exportar", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("# Todos mis cuadernos\n\n");

        for (Notebook notebook : notebooks) {
            content.append("## ").append(notebook.getTitle()).append("\n\n");
            if (notebook.getContent() != null) {
                content.append(notebook.getContent()).append("\n\n---\n\n");
            }
        }

        exportToPdf(context, "Todos mis cuadernos", content.toString());
    }

    private static String convertMarkdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }

        String html = markdown.replaceAll("```([\\s\\S]*?)```", "<pre><code>$1</code></pre>");

        // Procesar encabezados
        html = html
                .replaceAll("(?m)^######\\s+(.*?)\\s*$", "<h6>$1</h6>")
                .replaceAll("(?m)^#####\\s+(.*?)\\s*$", "<h5>$1</h5>")
                .replaceAll("(?m)^####\\s+(.*?)\\s*$", "<h4>$1</h4>")
                .replaceAll("(?m)^###\\s+(.*?)\\s*$", "<h3>$1</h3>")
                .replaceAll("(?m)^##\\s+(.*?)\\s*$", "<h2>$1</h2>")
                .replaceAll("(?m)^#\\s+(.*?)\\s*$", "<h1>$1</h1>");

        // Procesar formatos de texto
        html = html
                .replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>")
                .replaceAll("__(.*?)__", "<strong>$1</strong>")
                .replaceAll("\\*(.*?)\\*", "<em>$1</em>")
                .replaceAll("_(.*?)_", "<em>$1</em>")
                .replaceAll("`(.*?)`", "<code>$1</code>");

        // Procesar saltos de línea y párrafos
        html = html
                .replaceAll("(\\r?\\n){2,}", "</p><p>")
                .replaceAll("\\r?\\n", "<br>");

        // Asegurar que el texto esté envuelto en párrafos
        if (!html.startsWith("<p>") && !html.startsWith("<h") && !html.startsWith("<pre>")) {
            html = "<p>" + html;
        }
        if (!html.endsWith("</p>") && !html.endsWith("</h") && !html.endsWith("</pre>")) {
            html = html + "</p>";
        }

        return html;
    }

    public static void saveAsPdf(Context context, String title, String content, String filename) {
        if (context == null || title == null || content == null || filename == null) {
            Toast.makeText(context, "Datos inválidos para guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        String htmlContent = convertMarkdownToHtml(content);
        String pdfContent = "<!DOCTYPE html>" +
                "<html><head>" +
                "<title>" + title + "</title>" +
                "<meta charset=\"UTF-8\">" +
                "</head><body>" + htmlContent + "</body></html>";

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloadsDir.exists() && !downloadsDir.mkdirs()) {
            Toast.makeText(context, "No se pudo crear el directorio", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(downloadsDir, filename + ".html"); // Cambiado a .html para prueba

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(pdfContent.getBytes());
            Toast.makeText(context, "Archivo guardado en: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error al guardar archivo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}