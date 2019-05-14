
package com.example.stegocipher.steganogarficipher;

import com.example.stegocipher.steganogarficipher.algorithm.LSB;
import com.example.stegocipher.steganogarficipher.algorithm.HillChiper;
import com.example.stegocipher.steganogarficipher.algorithm.VigenereCipher;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DecodeActivity extends Activity {

	private Context context;
	private Handler handler;
	private ProgressDialog dd;
	private Bitmap sourceBitmap;
	private String absoluteFilePathSource ;
	private Uri photoUri;
	private TextView txtVigenereDekrip,txtHillCipher,txtPesanRahasia,txtLokasiGambar;
	private EditText txtKey;
	private Button btnPilihGambar,btnDekripsi,btnExtract;
	private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=1;
	private static final int PICK_IMAGE = 1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.decoder);
		context = this;

		txtKey = (EditText) this.findViewById(R.id.txtKey);
		txtLokasiGambar=(TextView) this.findViewById(R.id.txtLokasiGambar);
		txtPesanRahasia= (TextView) this.findViewById(R.id.txtpesanrahasia);
		txtVigenereDekrip = (TextView) this.findViewById(R.id.txtVigenereDekrip);
		txtHillCipher = (TextView) this.findViewById(R.id.txthillcipher);

		clearText();

		btnPilihGambar=(Button) this.findViewById(R.id.btnPilihGambar);
		btnDekripsi=(Button) this.findViewById(R.id.btnDekripsi);
		btnExtract=(Button) this.findViewById(R.id.btnExtract);

		btnPilihGambar.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {

				if (Build.VERSION.SDK_INT >= 23){
					// Here, thisActivity is the current activity

					if (ActivityCompat.checkSelfPermission(DecodeActivity.this,
							Manifest.permission.READ_EXTERNAL_STORAGE)
							!= PackageManager.PERMISSION_GRANTED) {

						// Should we show an explanation?
						if (ActivityCompat.shouldShowRequestPermissionRationale(DecodeActivity.this,
								Manifest.permission.READ_EXTERNAL_STORAGE)) {

							// Show an expanation to the user *asynchronously* -- don't block
							// this thread waiting for the user's response! After the user
							// sees the explanation, try again to request the permission.

						} else {

							// No explanation needed, we can request the permission.

							ActivityCompat.requestPermissions(DecodeActivity.this,
									new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
									MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

							// MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
							// app-defined int constant. The callback method gets the
							// result of the request.
						}
					}else{
						ActivityCompat.requestPermissions(DecodeActivity.this,
								new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
								MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
					}
				}else {

					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					photoPickerIntent.setType("image/*");
					startActivityForResult(photoPickerIntent, PICK_IMAGE);

				}

			}

		});

		/*TOMBOL DEKRIPSI*/
		btnDekripsi.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				/*PENGECEKAN KUNCI*/
				if(txtKey.getText().toString().equals("")){
					Toast.makeText(context, "Input kunci dahulu", Toast.LENGTH_SHORT).show();
				}else if (!txtKey.getText().toString().matches("[A-Za-z0-9]+")) {
					Toast.makeText(context, "Key harus mengandung karakter alphanumeric saja", Toast.LENGTH_SHORT).show();
				}else if(txtHillCipher.getText().toString().equals("")){
					Toast.makeText(context,"Tidak terdapat pesan rahasia ", Toast.LENGTH_SHORT).show();
				}else{

					int length_key=txtKey.getText().toString().length();
					double sq = Math.sqrt(length_key);

					if(sq != (long) sq) {
						Toast.makeText(context,"Panjang kunci bukan bilangan kuadrat ", Toast.LENGTH_SHORT).show();
					}else{
						/***PROSES DEKRIPSI HILL CIPHER */
						HillChiper hillchiper = new HillChiper();

						int s = (int) sq;

						if (hillchiper.check(txtKey.getText().toString(), s))
						{
							hillchiper.cofact(hillchiper.keymatrix, s);
							String invertkey=hillchiper.getmInverseKey();
							if (invertkey.equals("")){
								Toast.makeText(context,"Invalid key, invertible", Toast.LENGTH_SHORT).show();
								return;
							}else{
								hillchiper.check(invertkey, s);
								hillchiper.divide(txtHillCipher.getText().toString(), s);
								String hilldekrip=hillchiper.getmResult();
								txtVigenereDekrip.setText(hilldekrip);
							}
							/***END PROSES */

							/***PROSES DEKRIPSI DENGAN VIGENERE CIPHER */
							String pesanrahasia=VigenereCipher.decodeVigenereChiper(txtKey.getText().toString(), txtVigenereDekrip.getText().toString());
							txtPesanRahasia.setText(pesanrahasia);
							/***END PROSES */

						}else{

							if (hillchiper.getmError()==1)
								Toast.makeText(context,"Invalid key, determinant=0", Toast.LENGTH_SHORT).show();
							else if(hillchiper.getmError()==2)
								Toast.makeText(context,"Invalid key, determinant punya GCD yang sama dengan 95", Toast.LENGTH_SHORT).show();
						}
					}


				}

			}

		});

		/*TOMBOL EXTRACT DITEKAN*/
		btnExtract.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				closeContextMenu();
				closeOptionsMenu();

				/*panggil method extract*/
				extract();

			}
		});

	}

	private void clearText(){
		txtHillCipher.setText("");
		txtVigenereDekrip.setText("");
		txtPesanRahasia.setText("");
	}


	private void extract(){
		class ExtractData extends AsyncTask<Void,Void,String> {

			ProgressDialog loading;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				loading = ProgressDialog.show(context, "Extract Text", "Menunggu...", false, false);
			}

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				if (s == null) {
					handler.post(new Runnable() {

						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									context);
							builder.setMessage(
									context.getText(R.string.errorNoMobistegoImage))
									.setCancelable(false).setPositiveButton(
									context.getText(R.string.ok),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											clearText();
										}
									});

							AlertDialog alert = builder.create();
							alert.show();
						}
					});
				} else {
					//Log.v("Coded message", s);
					txtHillCipher.setText(s);
					loading.dismiss();
				}
			}


			@Override
			protected String doInBackground(Void... params) {
				int[] pixels = new int[sourceBitmap.getWidth() * sourceBitmap.getHeight()];
				sourceBitmap.getPixels(pixels, 0, sourceBitmap.getWidth(), 0, 0, sourceBitmap.getWidth(),
						sourceBitmap.getHeight());
				byte[] b = null;
				try {
					b = LSB.convertArray(pixels);
				} catch (OutOfMemoryError er) {
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setMessage(context.getText(R.string.errorImageTooLarge))
							.setCancelable(false).setPositiveButton(
							context.getText(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
													int id) {
									DecodeActivity.this.finish();
								}
							});

					AlertDialog alert = builder.create();
					alert.show();

				}
				/*PROSES EXTRACT LSB*/
				return LSB.decodeMessage(b, sourceBitmap.getWidth(), sourceBitmap.getHeight());
			}
		}
		ExtractData gj = new ExtractData();
		gj.execute();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.
					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					photoPickerIntent.setType("image/*");
					startActivityForResult(photoPickerIntent, PICK_IMAGE);
				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
									Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		switch (requestCode) {

			case (PICK_IMAGE):
				if (resultCode == RESULT_OK) {

					Uri photoUri = intent.getData();
					if (photoUri != null) {
						try {


							Cursor cursor = getContentResolver().query(
									photoUri, null, null, null, null);
							cursor.moveToFirst();

							int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
							absoluteFilePathSource = cursor.getString(idx);
							txtLokasiGambar.setText(absoluteFilePathSource);

							int i = absoluteFilePathSource.lastIndexOf('.');
							String typefile="";
							if (i > 0) {
								typefile = absoluteFilePathSource.substring(i+1).toLowerCase();
							}
							/*PENGECEKAN TYPE GAMBAR*/
							if(typefile.equals("jpg")||typefile.equals("jpeg")||typefile.equals("png")){

							BitmapFactory.Options opt = new BitmapFactory.Options();
							opt.inDither = false;
							opt.inScaled = false;
							opt.inDensity = 0;
							opt.inJustDecodeBounds = false;
							opt.inPurgeable = false;
							opt.inSampleSize = 1;
							opt.inScreenDensity = 0;
							opt.inTargetDensity = 0;

							sourceBitmap = BitmapFactory.decodeFile(absoluteFilePathSource, opt);
							}else{
								AlertDialog.Builder builder = new AlertDialog.Builder(context);
								builder.setMessage("Type file yang diperbolehkan hanya png,jpg,jpeg ")
										.setCancelable(false).setPositiveButton(
										context.getText(R.string.ok),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {

											}
										});

								AlertDialog alert = builder.create();
								alert.show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				break;

		}
	}


	public void run() {
		Bitmap image = null;
		try {
			Cursor cursor = getContentResolver().query(photoUri, null, null,
					null, null);
			cursor.moveToFirst();

			int idx = cursor
					.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			String absoluteFilePath = cursor.getString(idx);



		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}
