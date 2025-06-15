package upvictoria.pm_may_ago_2025.iti_271415.pi1u1.notion;

import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;

public class MainActivity extends AppCompatActivity {

    private EditText markdownEditor;
    private Markwon markwon;
    private MarkwonEditor markwonEditor;
    private boolean isShiftPressed = false;
    private int lastRenderPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        markdownEditor = findViewById(R.id.markdownEditor);

        // Configurar Markwon
        markwon = Markwon.builder(this)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TaskListPlugin.create(this))
                .build();

        markwonEditor = MarkwonEditor.create(markwon);

        // Listener para Enter
        markdownEditor.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    processCurrentBlock();
                    return true;
                }
                return false;
            }
        });

        // Listener para cambios
        markdownEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                // Auto-render para bloques de código
                if (editable.toString().endsWith("```\n")) {
                    processCurrentBlock();
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void processCurrentBlock() {
        try {
            String fullText = markdownEditor.getText().toString();
            int cursorPos = markdownEditor.getSelectionStart();

            // 1. Separar el texto
            String renderedText = fullText.substring(0, lastRenderPosition);
            String newText = fullText.substring(lastRenderPosition);

            // 2. Procesar solo el nuevo bloque
            Spanned newMarkdown = markwon.toMarkdown(newText);

            // 3. Crear el texto final CORREGIDO:
            SpannableStringBuilder finalText = new SpannableStringBuilder();
            finalText.append(renderedText); // Texto ya renderizado (como String)
            finalText.append(newMarkdown);  // Nuevo texto formateado (como Spanned)

            // 4. Aplicar el texto completo
            markwon.setParsedMarkdown(markdownEditor, finalText);

            // 5. Actualizar posición y añadir separación
            lastRenderPosition = cursorPos;
            markdownEditor.getText().insert(cursorPos, "\n");
            markdownEditor.setSelection(cursorPos + 1);

        } catch (Exception e) {
            Log.e("MARKDOWN", "Error en bloques", e);
        }
    }
}