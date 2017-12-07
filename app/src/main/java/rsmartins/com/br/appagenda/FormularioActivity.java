package rsmartins.com.br.appagenda;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.File;

import rsmartins.com.br.appagenda.dao.AlunoDAO;
import rsmartins.com.br.appagenda.modelo.Aluno;

public class FormularioActivity extends AppCompatActivity {

    public static final int CODIGO_CAMERA = 567;
    public static final String AUTHORITY = "rsmartins.com.br.appagenda";
    private FormularioHelper formularioHelper;
    private String caminhoFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        formularioHelper = new FormularioHelper(this);

        final Intent intent = getIntent();
        Aluno aluno = (Aluno) intent.getSerializableExtra("Aluno");
        if (aluno != null){
            formularioHelper.preencheFormulario(aluno);
        }

        Button btnFoto = (Button) findViewById(R.id.formulario_btn_foto);

        btnFoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                caminhoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
                File arquivoFoto = new File(caminhoFoto);

                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(FormularioActivity.this,
                                BuildConfig.APPLICATION_ID + ".provider", arquivoFoto));

                startActivityForResult(intentCamera, CODIGO_CAMERA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CODIGO_CAMERA) {
            if(requestCode == Activity.RESULT_OK){
                formularioHelper.carregaImagem(caminhoFoto);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_formulario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_formulario_ok:
                Aluno aluno = formularioHelper.getAluno();
                AlunoDAO alunoDAO = new AlunoDAO(this);

                if (aluno.getId() != null){
                    alunoDAO.altera(aluno);
                } else {
                    alunoDAO.insere(aluno);
                }
                alunoDAO.close();
                Toast.makeText(
                        FormularioActivity.this,
                        "Aluno " + aluno.getNome() + " salvo!",
                            Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
