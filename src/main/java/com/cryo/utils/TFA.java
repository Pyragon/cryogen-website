package com.cryo.utils;

import com.cryo.Website;
import com.cryo.entities.accounts.Account;
import com.cryo.entities.annotations.Endpoint;
import com.cryo.entities.annotations.EndpointSubscriber;
import com.cryo.modules.account.AccountUtils;
import com.google.common.io.ByteStreams;
import com.google.common.net.MediaType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import lombok.Cleanup;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import spark.Request;
import spark.Response;

import java.io.*;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

import static com.cryo.utils.Utilities.error;
import static com.cryo.utils.Utilities.renderPage;

@EndpointSubscriber
public class TFA {

    @Endpoint(method = "POST", endpoint = "/account/tfa/code")
    public static String getQRCode(Request request, Response response) {
        Account account = AccountUtils.getAccount(request);
        if(account == null)
            return error("Unable to retrieve QR Code. Please refresh the page and try again.");
        if(account.getTFAKey() == null)
            return error("You do not have TFA enabled. Please enable it first and try again.");
        File file = new File("./data/qr_codes/"+account.getUsername()+".png");
        if(!file.exists()) {
            try {
                String barcode = TFA.getGoogleAuthenticatorBarCode(account.getTFAKey(), account.getUsername(), "Cryogen");
                TFA.createQRCode(barcode, "./data/qr_codes/"+account.getUsername()+".png", 150, 150);
            } catch(Exception e) {
                e.printStackTrace();
                return error("Error building QR Code. Please refresh the page and try again. Report this bug via Github if it persists.");
            } finally {
                if(!file.exists())
                    return error("Error building QR Code. Please refresh the page and try again. Report this bug via Github if it persists.");
            }
        }
        try {
            Properties prop = new Properties();
            prop.put("success", true);
            prop.put("image", Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file)));
            return Website.getGson().toJson(prop);
        } catch (IOException ex) {
            ex.printStackTrace();
            return error("Error building QR Code. Please refresh the page and try again. Report this bug via Github if it persists.");
        }
    }

    @Endpoint(method = "POST", endpoint = "/account/tfa/noty")
    public static String getQRCodeNoty(Request request, Response response) {
        return renderPage("account/sections/tfa-noty", null, request, response);
    }

    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void createQRCode(String barCodeData, String filePath, int height, int width) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
    }

    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }
}
