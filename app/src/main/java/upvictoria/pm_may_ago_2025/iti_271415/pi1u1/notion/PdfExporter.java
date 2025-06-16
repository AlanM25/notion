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
            @Override
            public void onPageFinished(WebView view, String url) {
                createWebPrintJob(view, context, title);
            }
        });

        String htmlDoc = "<!DOCTYPE html>" +
                "<html><head>" +
                "<meta charset='UTF-8'>" +
                "<title>" + title + "</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "h1 { color: #2c3e50; border-bottom: 1px solid #eee; padding-bottom: 10px; }" +
                "h2 { color: #34495e; margin-top: 25px; }" +
                ".page-content { background: #f8f9fa; padding: 15px; border-radius: 4px; margin: 10px 0; }" +
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

    public static void exportNotebooksToPdf(Context context, List<Notebook> notebooks, DatabaseRepository repository) {
        if (notebooks == null || notebooks.isEmpty()) {
            Toast.makeText(context, "No hay cuadernos para exportar", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("# Listado de Cuadernos\n\n");

        for (Notebook notebook : notebooks) {
            // Título del cuaderno
            content.append("## ").append(notebook.getTitle()).append("\n");

            // Obtener páginas del cuaderno
            List<Page> pages = repository.getPagesByNotebook(notebook.getId());

            if (pages != null && !pages.isEmpty()) {
                content.append("**Páginas:**\n");
                for (Page page : pages) {
                    content.append("- ").append(page.getTitle()).append("\n");
                }
            } else {
                content.append("_No contiene páginas_\n");
            }
            content.append("\n"); // Espacio entre cuadernos
        }

        exportToPdf(context, "Listado de Cuadernos", content.toString());
    }

    private static String convertMarkdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) return "";

        return markdown
                .replaceAll("(?m)^#\\s+(.+)$", "<h1 style='color:#2c3e50;'>$1</h1>")
                .replaceAll("(?m)^##\\s+(.+)$", "<h2 style='color:#34495e;margin-top:15px;'>$1</h2>")
                .replaceAll("(?m)^-\\s+(.+)$", "<li style='margin-left:20px;'>$1</li>")
                .replaceAll("(?m)^\\*\\*(.+?)\\*\\*", "<strong>$1</strong>")
                .replaceAll("(?m)^_(.+?)_", "<em>$1</em>")
                .replaceAll("\n", "<br>");
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