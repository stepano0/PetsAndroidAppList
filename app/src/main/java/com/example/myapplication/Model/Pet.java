package com.example.myapplication.Model;


public class Pet {
    private long id;
    private String petName;
    private String petBreed;
    private int petGender;
    private int petWeight;
    private String petStrGender;

    public Pet() {

    }

    public Pet(String petName, String petBreed, int petGender, int petWeight) {
        this.petName = petName;
        this.petBreed = petBreed;
        this.petGender = petGender;
        this.petWeight = petWeight;
        this.petStrGender = getStrGender();
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getPetName() {
        return petName;
    }
    public void setPetName(String petName) {
        this.petName = petName;
    }
    public String getPetBreed() {
        return petBreed;
    }
    public void setPetBreed(String petBreed) {
        this.petBreed = petBreed;
    }
    public int getPetGender() {
        return petGender;
    }
    public void setPetGender(int petGender) {
        this.petGender = petGender;
    }
    public int getPetWeight() {
        return petWeight;
    }
    public void setPetWeight(int petWeight) {
        this.petWeight = petWeight;
    }

    public String getStrGender(){
        String genderStr = "UNKNOWN";
        switch (petGender) {
            case (1): genderStr = "MALE"; break;
            case (2): genderStr = "FEMALE"; break;
        }
        return genderStr;
    }
    @Override
    public String toString() {
        return "petName='" + petName + ' ' +", petBreed='" + petBreed + ' ' +", petGender=" + petStrGender +", petWeight=" + petWeight;
    }
}
