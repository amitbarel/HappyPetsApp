package dev.happypets.Objects;

public class Vet extends User{

    private String clinicAddress;
    private String animalSpeciality;

    public Vet() {
    }

    public Vet(String clinicAddress, String animalSpeciality) {
        super();
        this.clinicAddress = clinicAddress;
        this.animalSpeciality = animalSpeciality;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public Vet setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
        return this;
    }

    public String getAnimalSpeciality() {
        return animalSpeciality;
    }

    public Vet setAnimalSpeciality(String animalSpeciality) {
        this.animalSpeciality = animalSpeciality;
        return this;
    }
}
