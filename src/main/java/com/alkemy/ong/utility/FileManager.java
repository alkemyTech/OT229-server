package com.alkemy.ong.utility;

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
     * Converts a multi-part file into an in-memory java io File.
     * @param multipartFile the multi-part file, received from an endpoint.
     * @return  an in-memory Java file.
     * @throws IOException  if the original file name can't be retrieved from the multi-part file.
     */
    public static File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
        File simpleFile = new File(multipartFile.getOriginalFilename());
        FileOutputStream fileOutputStream = new FileOutputStream(simpleFile);
        fileOutputStream.write(multipartFile.getBytes());
        fileOutputStream.close();
        return simpleFile;
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
     * @throws IOException  if the original file name can't be retrieved from the multi-part file.
     */
    public static File convertBase64MultipartToFile(MultipartFile multipartFile) throws IOException {
        File simpleFile = new File(multipartFile.getOriginalFilename());
        FileOutputStream fileOutputStream = new FileOutputStream(simpleFile);
        fileOutputStream.write(Base64.getDecoder().decode( multipartFile.getBytes() ));
        fileOutputStream.close();
        return simpleFile;
    }

}
