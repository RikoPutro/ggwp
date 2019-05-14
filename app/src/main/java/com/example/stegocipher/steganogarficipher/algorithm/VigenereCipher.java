package com.example.stegocipher.steganogarficipher.algorithm;


public class VigenereCipher {

    //mulai dari ASCII char no 32 decimal
    public static int startASCIIchar=32;

    //jumlah karakter yang dikenal untuk enkripsi dari ASCII no. 32 ke atas yaitu sebanyak 95 character
    public static int numberOfChar=95;


    /**
     * Method ini mengenkripsi pesan asli.
     * @param katakunci adalah katakunci.
     * @param pesan adalah pesan asli.
     * @return chipertext dengan enkripsi algoritma VigenereChiper.
     */
    public static String encodeVigenereChiper(String katakunci,String pesan) {

        //trim all --> hilangkan spasi di depan dan belakang kalimat
        pesan=pesan.trim();
        katakunci=katakunci.trim();

        //jika panjang kata kunci < pesan, maka ulang katakunci hingga=pesan.
        if (katakunci.length()<pesan.length()){
            int pjg_kunci=katakunci.length();
            int pjg_pesan=pesan.length();

            int idx=0;
            for (int i = pjg_kunci; i < pjg_pesan; i++) {
                katakunci=katakunci+ katakunci.charAt(idx);
                idx++;
            }
            //jika panjang kata kunci > pesan, maka katakunci dipotong hingga=pesan.
        }else if(katakunci.length()>pesan.length()){
            katakunci=katakunci.substring(0, pesan.length());
        }

        //inisialisasi variable menampung chipertext
        String chipertext="";
        StringBuilder SBchipertext=new StringBuilder();

        //lakukan proses enkripsi vigenere chipere
        for (int i = 0; i < pesan.length(); i++) {
            int idxpesan=(pesan.charAt(i))-startASCIIchar;
            int idxkatakunci=(katakunci.charAt(i))-startASCIIchar;
            //chiper char adalah (idxpesan+idxkatakunci) % numberOfChar
            int idxchiper= ((idxpesan+idxkatakunci) % numberOfChar)+startASCIIchar;
            //gabungkan chiper char tersebut hingga membentuk suatu chipertext
            SBchipertext.append((char)idxchiper);
        }

        //konversi string builder ke string
        chipertext=SBchipertext.toString();
        return chipertext;

    }

    /**
     * Method ini mendekripsi chiper text.
     * @param katakunci adalah katakunci.
     * @param chipertext adalah chipertext.
     * @return pesan asli dengan dekripsi algoritma VigenereChiper.
     */

    public static String decodeVigenereChiper(String katakunci,String chipertext) {

        //trim all --> hilangkan spasi di depan dan belakang kalimat
        chipertext=chipertext.trim();
        katakunci=katakunci.trim();

        //jika panjang kata kunci < pesan, maka ulang katakunci hingga=pesan.
        if (katakunci.length()<chipertext.length()){
            int pjg_kunci=katakunci.length();
            int pjg_pesan=chipertext.length();

            int idx=0;
            for (int i = pjg_kunci; i < pjg_pesan; i++) {
                katakunci=katakunci+ katakunci.charAt(idx);
                idx++;
            }
            //jika panjang kata kunci > pesan, maka katakunci dipotong hingga=pesan.
        }else if(katakunci.length()>chipertext.length()){

            katakunci=katakunci.substring(0, chipertext.length());
        }

        //inisialisasi variable pesan asli
        String pesan="";
        StringBuilder SBpesan= new StringBuilder();

        for (int i = 0; i < chipertext.length(); i++) {

            int idxchiper=(chipertext.charAt(i))-startASCIIchar;
            int idxkatakunci=(katakunci.charAt(i))-startASCIIchar;
            int idxpesan=  (idxchiper-idxkatakunci);

            //jika hasil index negatif, maka ditambahkan sebanyak numberOfChar
            if (idxpesan<0) {
                idxpesan=idxpesan+numberOfChar;
            }

            //idxpesn ditambah startASCIIchar
            idxpesan=idxpesan+startASCIIchar;

            //tambahkan pesan ke stringbuilder
            SBpesan.append((char)idxpesan);
        }

        //konversi stringbuilder ke string
        pesan=SBpesan.toString();
        return pesan;

    }
}
