package application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import application.model.*;
import application.service.*;
import view.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class ControllerPrescriptionCreate {

    @Autowired
    PrescriptionRepository prescriptionRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    DrugRepository drugRepository;

    @Autowired
    SequenceService sequence;
    /*
     * Request for new prescription form.
     */
    @GetMapping("/prescription/new")
    public String getPrescriptionForm(Model model) {
        // return blank form for new patient registration
        model.addAttribute("prescription", new PrescriptionView());
        return "prescription_create";
    }

    @PostMapping("prescription")
    public String createPrescription(PrescriptionView p, Model model) {

        Doctor d = doctorRepository.findByIdAndLastName(p.getDoctorId(), p.getDoctorLastName());
        if(d == null){
            model.addAttribute("message", "Doctor not found.");
            model.addAttribute("prescription", p);
            return "prescription_create";
        }

        Patient patient = patientRepository.findByIdAndLastName(p.getPatientId(),p.getPatientLastName());
        if(patient == null){
            model.addAttribute("message", "Patient not found.");
            model.addAttribute("prescription", p);
            return "prescription_create";
        }

        Drug drug = drugRepository.findByName(p.getDrugName());
        if(drug == null){
            model.addAttribute("message", "Drug not found.");
            model.addAttribute("prescription", p);
            return "prescription_create";
        }


        // get the next unique id for prescription
        int id = sequence.getNextSequence("RXID_SEQUENCE");

        // create a model.prescription instance
        // copy data from PrescriptionView to model
        Prescription prescriptionModel = new Prescription();
        prescriptionModel.setRxid(id);
        prescriptionModel.setDoctorId(p.getDoctorId());
        prescriptionModel.setPatientId(p.getPatientId());
        prescriptionModel.setDrugName(p.getDrugName());
        prescriptionModel.setQuantity(p.getQuantity());
        prescriptionModel.setRefills(p.getRefills());
        // dates are stored as strings throughout this project
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date rxDate = new Date();
        prescriptionModel.setDateCreated(dateFormat.format(rxDate));



        p.setRxid(id);
        p.setRefillsRemaining(p.getRefills());
        prescriptionRepository.insert(prescriptionModel);

        // display message and patient information
        model.addAttribute("message", "Registration successful.");
        p.setRxid(id);
        model.addAttribute("prescription", p);
        return "prescription_show";
    }


}
