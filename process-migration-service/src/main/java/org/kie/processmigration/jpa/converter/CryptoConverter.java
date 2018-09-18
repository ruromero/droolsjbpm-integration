/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.processmigration.jpa.converter;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.kie.processmigration.model.exceptions.CredentialsException;

@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    private static final int AES_BLOCKSIZE = 128;
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5PADDING";

    private String password;

    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public CryptoConverter() {
        this.password = System.getProperty("credentials.password");
        initCiphers();
    }

    private void initCiphers() {
        try {
            if (password.getBytes().length != AES_BLOCKSIZE / 8) {
                throw new CredentialsException("Key length must be " + AES_BLOCKSIZE + " bytes. Received: " + password);
            }
            SecretKey secretKey = new SecretKeySpec(password.getBytes(), ALGORITHM);
            encryptCipher = Cipher.getInstance(TRANSFORMATION);
            byte[] iv = new byte[AES_BLOCKSIZE / 8];
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            decryptCipher = Cipher.getInstance(TRANSFORMATION);
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize ciphers", e);
        }
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return Base64.getEncoder().encodeToString(encryptCipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Unable to encrypt credentials", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return new String(decryptCipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new RuntimeException("Unable to decrypt credentials", e);
        }
    }

}
