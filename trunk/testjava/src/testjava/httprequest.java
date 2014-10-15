package testjava;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Random;
import java.util.zip.GZIPInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class httprequest {
		
    public static void main(String[] args) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	String spectatorServer = "http://110.45.191.11/";
        	String nation = "KR";
        	String gameId = "1546995639";
        	String encryptionKey = "tvB2L04R/KlaIoviYDDzza1sVeVcsHYJ";
        	String rand = "-";
        	
        	Random rd = new Random();
        	for(int i=0;i<19;i++) {
        		rand += rd.nextInt(10);
        	}
        	
            HttpGet httpget = new HttpGet(spectatorServer+"observer-mode/rest/consumer/version");
            CloseableHttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonMetaData, jsonLastChunkInfo;
            
            try {
                System.out.println("----------------------------------------");
                InputStream is = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
                System.out.println(response.getStatusLine());
                String version = br.readLine();
                
                httpget = new HttpGet(spectatorServer+"observer-mode/rest/consumer/getGameMetaData/"+nation+"/"+gameId+"/"+rand+"/token");
                response = httpclient.execute(httpget);
                System.out.println(response.getStatusLine());
                
                entity = response.getEntity();
                
                is = entity.getContent();
                br = new BufferedReader(new InputStreamReader(is));
                
                String gameMetaDataString = br.readLine();
                System.out.println(gameMetaDataString);
                jsonMetaData = (JSONObject) jsonParser.parse(gameMetaDataString);
                //encryptionKey = (String) jsonMetaData.get("encryptionKey");
                
                httpget = new HttpGet(spectatorServer+"observer-mode/rest/consumer/getLastChunkInfo/"+nation+"/"+gameId+"/30000/token");
                response = httpclient.execute(httpget);
                System.out.println(response.getStatusLine());
                
                entity = response.getEntity();
                
                is = entity.getContent();
                br = new BufferedReader(new InputStreamReader(is,"utf-8"));
                
                String lastChunkString = br.readLine();
                System.out.println(lastChunkString);
                jsonLastChunkInfo = (JSONObject) jsonParser.parse(lastChunkString);
                
                httpget = new HttpGet(spectatorServer+"observer-mode/rest/consumer/getGameDataChunk/"+nation+"/"+gameId+"/"+jsonLastChunkInfo.get("nextChunkId")+"/token");
                response = httpclient.execute(httpget);
                System.out.println(response.getStatusLine());
                entity = response.getEntity();
                is = entity.getContent();
                br = new BufferedReader(new InputStreamReader(is,"utf-8"));
                byte[] chunkDataBytes = IOUtils.toByteArray(is);
                
                System.out.println(chunkDataBytes.length);
                
                SecretKeySpec blowfishKey = new SecretKeySpec(gameId.getBytes("ASCII"), "Blowfish");
                Cipher blowfishCipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
                blowfishCipher.init(Cipher.DECRYPT_MODE, blowfishKey);
                Decoder dc = Base64.getDecoder();
                byte[] encryptKeyBytes = dc.decode(encryptionKey);
                byte[] encryptkeyDecryptedByGameId = blowfishCipher.doFinal(encryptKeyBytes);
                
                SecretKeySpec chunkSpec = new SecretKeySpec(encryptkeyDecryptedByGameId, "Blowfish");
                Cipher chunkCipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
                chunkCipher.init(Cipher.DECRYPT_MODE, chunkSpec);

                byte[] chunkContent = chunkDataBytes;
                byte[] chunkDecryptedBytes = chunkCipher.doFinal(chunkContent);
                
                ByteArrayInputStream bis = new ByteArrayInputStream(chunkDecryptedBytes);
                GZIPInputStream gzis = new GZIPInputStream(bis);
                FileOutputStream fos = new FileOutputStream(new File("d://java_output//lol_chunk"+jsonLastChunkInfo.get("nextChunkId")));
                
                int c;
                
                while((c=gzis.read()) != -1) {
                	fos.write(c);
                }

                httpget = new HttpGet(spectatorServer+"observer-mode/rest/consumer/getKeyFrame/"+nation+"/"+gameId+"/"+jsonLastChunkInfo.get("keyFrameId")+"/token");
                response = httpclient.execute(httpget);
                System.out.println(response.getStatusLine());
                entity = response.getEntity();
                is = entity.getContent();
                br = new BufferedReader(new InputStreamReader(is,"utf-8"));
                byte[] keyFrameBytes = IOUtils.toByteArray(is);
                
                httpget.abort();
                
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
    
    

}
