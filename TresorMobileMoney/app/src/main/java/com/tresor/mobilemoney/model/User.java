package com.tresor.mobilemoney.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Priscilla on 11/09/2017.
 */

public class User {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    @SerializedName("id") // param from API
    public int userID;

    @SerializedName("...")
    public String userName;

    @SerializedName("...")
    public String userIDDate;

    @SerializedName("...")
    public String userIDLocality;

    @SerializedName("...")
    public String userDuplicataDate;

    @SerializedName("...")
    public String userDuplicataLocality;

    @SerializedName("...")
    public String userSituation;

    @SerializedName("...")
    public String userPhone;

    @SerializedName("...")
    public int userCode;

    @SerializedName("...")
    public String userFirstName;

    @SerializedName("...")
    public String userEmail;

    @SerializedName("...")
    public String userAddress;

    @SerializedName("...")
    public String userBirthDate;

    @SerializedName("...")
    public String userNationality;

    @SerializedName("...")
    public String userPieceRecto;

    @SerializedName("...")
    public String userPieceVerso;

    @SerializedName("...")
    public int userValidation;

    @SerializedName("...")
    public String userEmailStatus;

    @SerializedName("...")
    public String userDateRegister;

    @SerializedName("...")
    public String userDateRegisterValidation;

    @SerializedName("...")
    public String userPassword;

    @SerializedName("...")
    public String userPasswordHash;

    @SerializedName("...")
    public String userValidationUser;

    // ===========================================================
    // Constructors
    // ===========================================================

    public User(int userID, String userName, String userIDDate, String userIDLocality,
                String userDuplicataDate, String userDuplicataLocality, String userSituation,
                String userPhone, int userCode, String userFirstName, String userEmail,
                String userAddress, String userBirthDate, String userNationality,
                String userPieceRecto, String userPieceVerso, int userValidation,
                String userEmailStatus, String userDateRegister, String userDateRegisterValidation,
                String userPassword, String userPasswordHash, String userValidationUser) {

        this.userID = userID;
        this.userName = userName;
        this.userIDDate = userIDDate;
        this.userIDLocality = userIDLocality;
        this.userDuplicataDate = userDuplicataDate;
        this.userDuplicataLocality = userDuplicataLocality;
        this.userSituation = userSituation;
        this.userPhone = userPhone;
        this.userCode = userCode;
        this.userFirstName = userFirstName;
        this.userEmail = userEmail;
        this.userAddress = userAddress;
        this.userBirthDate = userBirthDate;
        this.userNationality = userNationality;
        this.userPieceRecto = userPieceRecto;
        this.userPieceVerso = userPieceVerso;
        this.userValidation = userValidation;
        this.userEmailStatus = userEmailStatus;
        this.userDateRegister = userDateRegister;
        this.userDateRegisterValidation = userDateRegisterValidation;
        this.userPassword = userPassword;
        this.userPasswordHash = userPasswordHash;
        this.userValidationUser = userValidationUser;
    }


    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIDDate() {
        return userIDDate;
    }

    public void setUserIDDate(String userIDDate) {
        this.userIDDate = userIDDate;
    }

    public String getUserIDLocality() {
        return userIDLocality;
    }

    public void setUserIDLocality(String userIDLocality) {
        this.userIDLocality = userIDLocality;
    }

    public String getUserDuplicataDate() {
        return userDuplicataDate;
    }

    public void setUserDuplicataDate(String userDuplicataDate) {
        this.userDuplicataDate = userDuplicataDate;
    }

    public String getUserDuplicataLocality() {
        return userDuplicataLocality;
    }

    public void setUserDuplicataLocality(String userDuplicataLocality) {
        this.userDuplicataLocality = userDuplicataLocality;
    }

    public String getUserSituation() {
        return userSituation;
    }

    public void setUserSituation(String userSituation) {
        this.userSituation = userSituation;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserBirthDate() {
        return userBirthDate;
    }

    public void setUserBirthDate(String userBirthDate) {
        this.userBirthDate = userBirthDate;
    }

    public String getUserNationality() {
        return userNationality;
    }

    public void setUserNationality(String userNationality) {
        this.userNationality = userNationality;
    }

    public String getUserPieceRecto() {
        return userPieceRecto;
    }

    public void setUserPieceRecto(String userPieceRecto) {
        this.userPieceRecto = userPieceRecto;
    }

    public String getUserPieceVerso() {
        return userPieceVerso;
    }

    public void setUserPieceVerso(String userPieceVerso) {
        this.userPieceVerso = userPieceVerso;
    }

    public int getUserValidation() {
        return userValidation;
    }

    public void setUserValidation(int userValidation) {
        this.userValidation = userValidation;
    }

    public String getUserEmailStatus() {
        return userEmailStatus;
    }

    public void setUserEmailStatus(String userEmailStatus) {
        this.userEmailStatus = userEmailStatus;
    }

    public String getUserDateRegister() {
        return userDateRegister;
    }

    public void setUserDateRegister(String userDateRegister) {
        this.userDateRegister = userDateRegister;
    }

    public String getUserDateRegisterValidation() {
        return userDateRegisterValidation;
    }

    public void setUserDateRegisterValidation(String userDateRegisterValidation) {
        this.userDateRegisterValidation = userDateRegisterValidation;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPasswordHash() {
        return userPasswordHash;
    }

    public void setUserPasswordHash(String userPasswordHash) {
        this.userPasswordHash = userPasswordHash;
    }

    public String getUserValidationUser() {
        return userValidationUser;
    }

    public void setUserValidationUser(String userValidationUser) {
        this.userValidationUser = userValidationUser;
    }


    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================

}