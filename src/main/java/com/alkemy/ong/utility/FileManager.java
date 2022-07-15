package com.alkemy.ong.utility;

import com.alkemy.ong.exception.CorruptedFileException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;

/**
 * Utility class to manage common operations involving files.
 */
public abstract class FileManager {

    /**
     * Generates a file name with extra options provided using a builder.
     *
     * @param multipartFile a file
     * @return  a gemerated name for the file.
     */
    public static FileNameBuilder buildFileName(MultipartFile multipartFile) {
        return new FileNameBuilder( multipartFile.getOriginalFilename() );
    }

    /**
     * Generates a file name with extra options provided using a builder.
     *
     * @param fileName the file name.
     * @return  a gemerated name for the file.
     */
    public static FileNameBuilder buildFileName(String fileName) {
        return new FileNameBuilder( fileName );
    }

    /**
     * Converts a multi-part file into an in-memory java io File.
     * @param multipartFile the multi-part file, received from an endpoint.
     * @return  an in-memory Java file.
     * @throws CorruptedFileException  if the original file name can't be retrieved from the multi-part file, or if
     * the latter is corrupted.
     */
    public static File convertMultiPartToFile(MultipartFile multipartFile) throws CorruptedFileException {
        try {
            File simpleFile = new File(multipartFile.getOriginalFilename());
            FileOutputStream fileOutputStream = new FileOutputStream(simpleFile);
            fileOutputStream.write(multipartFile.getBytes());
            fileOutputStream.close();
            return simpleFile;
        } catch (IOException e) {
            throw new CorruptedFileException(e.getMessage(), e);
        }
    }

    /**
     * Simple builder for a file name with some options.
     */
    @Validated
    public static class FileNameBuilder {

        private String name;

        /**
         * Constructor.
         *
         * @param baseName  the base file name.
         */
        public FileNameBuilder(@NotNull String baseName) {
            this.name = baseName;
        }

        /**
         * Appends the current time to the file name.
         *
         * @return the file name with the current time in milliseconds appended at the end.
         */
        public FileNameBuilder withTimeStamp() {
            String timestamp = Long.toString( new Date().getTime() );
            this.name = timestamp + "-" + this.name;
            return this;
        }

        /**
         * Replaces all the spaces in the name with underscores.
         *
         * @return  the file name with underscores instead of spaces.
         */
        public FileNameBuilder withoutSpaces() {
            this.name = this.name.replace(" ", "_");
            return this;
        }

        /**
         * Generates the file name.
         *
         * @return  the final file name.
         */
        public String build() {
            return this.name;
        }

    }

    /**
     * Converts a base64 encoded multi-part file into an in-memory decoded java io File.
     *
     * @param multipartFile the multi-part file, received from an endpoint, which is encoded in base64.
     * @return  an in-memory Java file, decoded.
     * @throws CorruptedFileException  if the original file name can't be retrieved from the multi-part file, or if
     * the latter is corrupted.
     */
    public static File convertBase64MultipartToFile(MultipartFile multipartFile) throws CorruptedFileException {
        try {
            File simpleFile = new File(multipartFile.getOriginalFilename());
            FileOutputStream fileOutputStream = new FileOutputStream(simpleFile);
            fileOutputStream.write(Base64.getDecoder().decode( multipartFile.getBytes() ));
            fileOutputStream.close();
            return simpleFile;
        } catch (IOException e) {
            throw new CorruptedFileException(e.getMessage(), e);
        }
    }

    /**
     * Converts a base64 encoded multi-part file into an in-memory decoded java io File.
     *
     * @param encodedFile the encoded file in the resulting string format.
     * @param fileName the original file name, extension included.
     * @return  an in-memory Java file, decoded.
     * @throws CorruptedFileException  if the original file name can't be retrieved from the multi-part file, or if
     * the latter is corrupted.
     */
    public static File convertBase64StringToFile(String encodedFile, String fileName) throws CorruptedFileException {
        try {
            File simpleFile = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(simpleFile);
            fileOutputStream.write(Base64.getDecoder().decode(encodedFile));
            fileOutputStream.close();
            return simpleFile;
        } catch (IOException e) {
            throw new CorruptedFileException(e.getMessage(), e);
        }
    }

}
