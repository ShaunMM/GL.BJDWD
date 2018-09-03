package com.bjdwd.beans;

/**
 * Created by dell on 2017/5/24.
 */

public class File {
    private int Id;
    private String Extension;
    private int Size;
    private String Path;
    private String HashCode;
    private String UploadTime;
    private boolean IsDeleted;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getExtension() {
        return Extension;
    }

    public void setExtension(String Extension) {
        this.Extension = Extension;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int Size) {
        this.Size = Size;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String Path) {
        this.Path = Path;
    }

    public String getHashCode() {
        return HashCode;
    }

    public void setHashCode(String HashCode) {
        this.HashCode = HashCode;
    }

    public String getUploadTime() {
        return UploadTime;
    }

    public void setUploadTime(String UploadTime) {
        this.UploadTime = UploadTime;
    }

    public boolean isIsDeleted() {
        return IsDeleted;
    }

    public void setIsDeleted(boolean IsDeleted) {
        this.IsDeleted = IsDeleted;
    }
}
