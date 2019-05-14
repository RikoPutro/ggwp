
package com.example.stegocipher.steganogarficipher;

import com.example.stegocipher.steganogarficipher.algorithm.LSB;
import com.example.stegocipher.steganogarficipher.algorithm.HillChiper;
import com.example.stegocipher.steganogarficipher.algorithm.VigenereCipher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class EncodeActivity extends Activity {

	private Context context;
	private final Handler handler = new Handler();
	private Bitmap sourceBitmap;
	public static final int PICK_IMAGE = 1;
	private String absoluteFilePathSource ;
	private TextView txtHillCipher,txtVigenereCipher,txtLokasiGambar;
	private EditText txtPesanRahasia,txtKey;
	private ImageView imgEmbedd;
	private Button btnPilihGambar,btnEnkripsi,btnEmbedd,btnShare;
	private MobiProgressBar progressBar;
	private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=1;
	private Uri urishare;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encoder);

		context = this;
		txtLokasiGambar=(TextView) this.findViewById(R.id.txtLokasiGambar);
		txtKey = (EditText) this.findViewById(R.id.txtKey);
		txtPesanRahasia= (EditText) this.findViewById(R.id.txtpesanrahasia);
		txtHillCipher = (TextView) this.findViewById(R.id.txthillcipher);
		txtVigenereCipher = (TextView) this.findViewById(R.id.txtvigenerecipher);
		btnPilihGambar=(Button) this.findViewById(R.id.btnPilihGambar);
		btnEnkripsi=(Button) this.findViewById(R.id.btnEnkripsi);
		btnEmbedd=(Button) this.findViewById(R.id.btnEmbedd);
		btnShare=(Button) this.findViewById(R.id.btnShare);
		imgEmbedd=(ImageView) this.findViewById(R.id.imgEmbedd);


		btnShare.setEnabled(false);

		btnPilihGambar.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {

				if (Build.VERSION.SDK_INT >= 23){
					// Here, thisActivity is the current activity

					if (ActivityCompat.checkSelfPermission(EncodeActivity.this,
							Manifest.permission.READ_EXTERNAL_STORAGE)
							!= PackageManager.PERMISSION_GRANTED) {

						// Should we show an explanation?
						if (ActivityCompat.shouldShowRequestPermissionRationale(EncodeActivity.this,
								Manifest.permission.READ_EXTERNAL_STORAGE)) {

							// Show an expanation to the user *asynchronously* -- don't block
							// this thread waiting for the user's response! After the user
							// sees the explanation, try again to request the permission.

						} else {

							// No explanation needed, we can request the permission.

							ActivityCompat.requestPermissions(EncodeActivity.this,
									new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
									MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

							// MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
							// app-defined int constant. The callback method gets the
							// result of the request.
						}
					}else{
						ActivityCompat.requestPermissions(EncodeActivity.this,
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


		/*button enkripsi ditekan*/
		btnEnkripsi.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				/* validasi kunci */
				if (txtKey.getText().toString().equals("")) {
					Toast.makeText(context, "Masukkan kunci dahulu", Toast.LENGTH_SHORT).show();
				} else if (!txtKey.getText().toString().matches("[A-Za-z0-9]+")) {
					Toast.makeText(context, "Key harus mengandung karakter alphanumeric saja", Toast.LENGTH_SHORT).show();
				} else if (txtPesanRahasia.getText().toString().equals("")) {
					Toast.makeText(context, "Masukkan pesan rahasia dahulu ", Toast.LENGTH_SHORT).show();
				} else {

					int length_key = txtKey.getText().toString().length();
					double sq = Math.sqrt(length_key);

					if (sq != (long) sq) {
						Toast.makeText(context, "Panjang kunci bukan bilangan kuadrat ", Toast.LENGTH_SHORT).show();
					} else {

						HillChiper hillchiper = new HillChiper();

						int s = (int) sq;

						if (hillchiper.check(txtKey.getText().toString(), s)) {

							/***PROSES ENKRIPSI DENGAN VIGENERE CIPHER */
							String vigcipher = VigenereCipher.encodeVigenereChiper(txtKey.getText().toString(), txtPesanRahasia.getText().toString());
							txtVigenereCipher.setText(vigcipher);
							/***END PROSES */

							/***PROSES ENKRIPSI DENGAN HILL CIPHER */
							hillchiper.divide(txtVigenereCipher.getText().toString(), s);
							hillchiper.cofact(hillchiper.keymatrix, s);
							String key = hillchiper.getmInverseKey();
							if (key.equals("")) {
								Toast.makeText(context, "Invalid key, invertible", Toast.LENGTH_SHORT).show();
								txtVigenereCipher.setText("");
								return;
							} else {
								txtHillCipher.setText(hillchiper.getmResult());
							}
							/***END PROSES */

						} else {

							if (hillchiper.getmError() == 1)
								Toast.makeText(context, "Invalid key, determinant=0", Toast.LENGTH_SHORT).show();
							else if (hillchiper.getmError() == 2)
								Toast.makeText(context, "Invalid key, determinant punya GCD yang sama dengan 95", Toast.LENGTH_SHORT).show();
						}

					}

				}

			}

		});


		/*button embedd di tekan*/
		btnEmbedd.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {

				/*pengecekan apakah sudah dilakukan enkripsi*/
				if(txtHillCipher.getText().toString().equals("")){
					Toast.makeText(context, "Lakukan enkripsi terlebih dahulu", Toast.LENGTH_SHORT).show();
					return;
				}
				/*pengecekan apakah sudah memilih gambar*/
				if (sourceBitmap==null){
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setMessage(context.getText(R.string.errorPilihGambar))
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
				}else{

					closeContextMenu();
					closeOptionsMenu();
					progressBar=new MobiProgressBar(EncodeActivity.this);
					progressBar.setMax(100);
					progressBar.setMessage(context.getString(R.string.encoding));
					progressBar.show();
					Thread tt = new Thread(new Runnable() {
						public void run() {

							/*menjalankan proses embedding text ke gambar*/
							urishare=embedd();
							handler.post(mShowAlert);
						}
					});
					tt.start();
				}



			}
		});

		btnShare.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				Intent share = new Intent(Intent.ACTION_SEND);
				share.setData(urishare);
				share.setType("image/jpeg");
				share.putExtra(Intent.EXTRA_STREAM, urishare);
				startActivity(Intent.createChooser(share, "Share Image"));

			}
		});
	}

	/*jika sudah selesai embedding, tampilkan alert berhasil*/
    final Runnable mShowAlert = new Runnable() {
        public void run() {
        	progressBar.dismiss();
        	AlertDialog.Builder builder = new AlertDialog.Builder(
					context);
			builder.setMessage(context.getText(R.string.saved))
					.setCancelable(false).setPositiveButton(
					context.getText(R.string.ok),
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialog,
								int id) {
							btnShare.setEnabled(true);
							imgEmbedd.invalidate();
							imgEmbedd.setImageURI(urishare);
						}
					});

			AlertDialog alert = builder.create();
			alert.show();
        }
    };

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

							/*setelah user memilih salah satu foto dari gallery*/
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

							/*pengecekan type file gambar yang dipilih dari gallery */
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

								imgEmbedd.invalidate();
								btnShare.setEnabled(false);
								urishare=null;

							}else{

								/*jika type file tidak sesuai*/
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

	final Runnable mIncrementProgress = new Runnable() {
        public void run() {
            progressBar.incrementProgressBy(1);
        }
    };
    
    final Runnable mInitializeProgress = new Runnable() {
        public void run() {
            progressBar.setMax(100);
        }
    };
	
    final Runnable mSetInderminate= new Runnable() {
        public void run() {
        	progressBar.setMessage(context.getString(R.string.saving));
            progressBar.setIndeterminate(true);
        }
    };
    
    
	private Uri embedd() {

		Uri result=null;

		String s = txtHillCipher.getText().toString();

		int width = sourceBitmap.getWidth();
		int height = sourceBitmap.getHeight();

		int[] oneD = new int[width * height];
		sourceBitmap.getPixels(oneD, 0, width, 0, 0, width, height);
		int density=sourceBitmap.getDensity();
		sourceBitmap.recycle();

		/**PROSES EMBEDDING TEXT DENGAN LSB *****/
		byte[] byteImage = LSB.encodeMessage(oneD, width, height, s,
				new ProgressHandler() {
					private int mysize;
					private int actualSize;


					public void increment(final int inc) {
						actualSize += inc;
						if (actualSize % mysize == 0)
							handler.post(mIncrementProgress);
					}


					public void setTotal(final int tot) {
						mysize = tot / 50;
						handler.post(mInitializeProgress);
					}


					public void finished() {

					}
				});
		oneD=null;
		sourceBitmap=null;
		int[] oneDMod = LSB.byteArrayToIntArray(byteImage);
		byteImage=null;

		
		System.gc();

		/**PROSES PEMBENTUKAN BYTE YANG SUDAH DISISIPKAN MENJADI GAMBAR KEMBALI*/
		Bitmap destBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);

		destBitmap.setDensity(density);
		int partialProgr=height*width/50;
		int masterIndex = 0;
		for (int j = 0; j < height; j++)
			for (int i = 0; i < width; i++){
				// The unique way to write correctly the sourceBitmap, android bug!!!
				destBitmap.setPixel(i, j, Color.argb(0xFF,
						oneDMod[masterIndex] >> 16 & 0xFF,
						oneDMod[masterIndex] >> 8 & 0xFF,
						oneDMod[masterIndex++] & 0xFF));
				if(masterIndex%partialProgr==0)
					handler.post(mIncrementProgress);
			}
		handler.post(mSetInderminate);

		/*PROSES PENYIMPANAN GAMBAR KE MEMORY*/
		String sdcardState = android.os.Environment.getExternalStorageState();		
		String destPath = null;
		int indexSepar=absoluteFilePathSource.lastIndexOf(File.separator);
		int indexPoint=absoluteFilePathSource.lastIndexOf(".");
		if(indexPoint<=1)
			indexPoint=absoluteFilePathSource.length();

		String typefile = "";
		if (indexPoint > 0) {
			typefile = absoluteFilePathSource.substring(indexPoint+1);
		}
		String fileNameDest=absoluteFilePathSource.substring(indexSepar+1, indexPoint);
		fileNameDest+="_stego";
		if (sdcardState.contentEquals(android.os.Environment.MEDIA_MOUNTED))
			destPath = android.os.Environment.getExternalStorageDirectory()
					+ File.separator + fileNameDest+"."+typefile;

		OutputStream fout = null;
		try {

			fout = new FileOutputStream(destPath);			
			destBitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
			result=Uri.parse("file://"+destPath);
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result)); 
			fout.flush();
			fout.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		destBitmap.recycle();
		return result;
	}
}