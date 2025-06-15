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
        String cleanContent = content.replaceAll("  \n", "\n\n");
        String htmlContent = convertMarkdownToHtml(cleanContent);
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

        // Procesar saltos de línea primero (dos espacios + salto de línea para <br>)
        String html = markdown.replaceAll("  \n", "<br>");

        // Bloques de código
        html = html.replaceAll("```([\\s\\S]*?)```", "<pre><code>$1</code></pre>");

        // Encabezados
        html = html.replaceAll("(?m)^#\\s+(.*?)\\s*$", "<h1>$1</h1>")
                .replaceAll("(?m)^##\\s+(.*?)\\s*$", "<h2>$1</h2>");

        // Negritas y cursivas (con mejor manejo de espacios)
        html = html.replaceAll("\\*\\*(\\S(.*?)\\S)\\*\\*(?!\\*)", "<strong>$1</strong>")
                .replaceAll("\\*(\\S(.*?)\\S)\\*(?!\\*)", "<em>$1</em>");

        // Listas
        html = html.replaceAll("(?m)^-\\s+(.*?)$", "<li>$1</li>")
                .replaceAll("(?m)(<li>.*</li>)", "<ul>$1</ul>");

        // Párrafos (manejo más inteligente)
        html = html.replaceAll("(?m)^([^<\\n].*?)$", "<p>$1</p>")
                .replaceAll("<p>\\s*</p>", ""); // Eliminar párrafos vacíos

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