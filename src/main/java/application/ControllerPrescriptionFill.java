package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import application.model.*;
import view.*;

@Controller
public class ControllerPrescriptionFill {

	@Autowired
	DoctorRepository doctorRepository;

	@Autowired
	PatientRepository patientRepository;

	@Autowired
	PharmacyRepository pharmacyRepository;

	@Autowired
	PrescriptionRepository prescriptionRepository;

	/*
	 * Patient requests form to fill prescription.
	 */
	@GetMapping("/prescription/fill")
	public String getfillForm(Model model) {
		model.addAttribute("prescription", new PrescriptionView());
		return "prescription_fill";
	}

	// process data from prescription_fill form
	@PostMapping("/prescription/fill")
	public String processFillForm(PrescriptionView p, Model model) {
		// Set a variable to be updated by any refills
		int refillCount = 0;
		ArrayList<Pharmacy.DrugCost> drugCosts = new ArrayList<>();
		ArrayList<Prescription.FillRequest> fillRequests = new ArrayList<>();
		Prescription.FillRequest newFillRequest = new Prescription.FillRequest();

		// validate pharmacy name and address, get pharmacy id and phone
		Pharmacy ph = pharmacyRepository.findByNameAndAddress(p.getPharmacyName(), p.getPharmacyAddress());
		if (ph != null) {
			// Pharmacy found, copy any fields needed for the final form/page
			p.setPharmacyID(ph.getId());
			p.setPharmacyName(ph.getName());
			p.setPharmacyAddress(ph.getAddress());
			p.setPharmacyPhone(ph.getPhone());
			// List prices of drugs will be used later to validate inventory and populate the form
			drugCosts = ph.getDrugCosts();
			} else {
			// Pharmacy not found, notify the user
			model.addAttribute("message", "Pharmacy not found, please confirm pharmacy name and address.");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}

		// find the patient information
		Patient patient = patientRepository.findByLastName(p.getPatientLastName());
			if (patient != null) {
				p.setPatientId(patient.getId());
				p.setPatientFirstName(patient.getFirstName());
				p.setPatientLastName(patient.getLastName());
			}
			else{
				model.addAttribute("message", "There is no patient found with that last name. Please confirm spelling.");
				model.addAttribute("prescription", p);
				return "prescription_fill";
			}

		// find the prescription
		Prescription prescription = prescriptionRepository.findById(p.getRxid());
		if (prescription != null) {
			p.setRxid(prescription.getRxid());
			// dev note: the field 'refills' is displayed with the text 'refills remaining'. This is inconsistent
			// with the text display and corrections were made in 'prescription_show.html' to adjust it.
			p.setRefills(prescription.getRefills());
			p.setDoctorId(prescription.getDoctorId());
			p.setQuantity(prescription.getQuantity());
			p.setDrugName(prescription.getDrugName());
			// fill in our array to traverse any existing fill requests
			fillRequests = prescription.getFills();
			// We will assume that the size of the array is sufficient to count prescription fill occurrences
			refillCount = fillRequests.size();
			p.setRefillsRemaining(prescription.getRefills() - refillCount);
		} else {
			model.addAttribute("message", "That is not a valid prescription for " +
					p.getPatientFirstName() + " " + p.getPatientLastName() +". Please confirm and resubmit.");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}

		/*
		 * have we exceeded the number of allowed refills
		 * the first fill is not considered a refill.
		 */
		if (p.getRefillsRemaining() < 1) {
			model.addAttribute("message", "There are no remaining refills on prescription " + p.getRxid() + ".");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}

		/*
		 * get doctor information 
		 */
		Doctor doctor = doctorRepository.findByLastName(patient.getPrimaryName());
		if (doctor != null){
			p.setDoctorFirstName(doctor.getFirstName());
			p.setDoctorLastName(doctor.getLastName());
		}
		else{
			model.addAttribute("message", "Unable to find doctor: " + patient.getPrimaryName());
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}

		/*
		 * calculate cost of prescription
		 */
		if (!drugCosts.isEmpty()) {
			// Traverse the array of drugs that the pharmacy offers
            for (Pharmacy.DrugCost drugCost : drugCosts) {
                // if the name matches our request, update the price
                if (drugCost.getDrugName().equals(p.getDrugName())) {
                    // note that the cost is stored as double for the pharmacy but string for the filled prescription
                    p.setCost(Double.toString(drugCost.getCost()));
                    // assign a date for filling the prescription
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fillDate = new Date();
                    p.setDateFilled(dateFormat.format(fillDate));
                    // also add all this to our fill array for write back to the document
                    newFillRequest.setPharmacyID(p.getPharmacyID());
                    newFillRequest.setDateFilled(dateFormat.format(fillDate));   // writing a text string in yyyy-mm-dd form
                    newFillRequest.setCost(p.getCost());
                }
            }
		} else {
			model.addAttribute("message", p.getPharmacyName() + " does not carry " + p.getDrugName() + ". Please select another pharmacy.");
			model.addAttribute("prescription", p);
			return "prescription_fill";
		}

		// save updated prescription
		p.setRefillsRemaining(p.getRefillsRemaining()-1);	// decrement the refill counter
		fillRequests.add(newFillRequest);					// add our new fill to the fill list
		prescription.setFills(fillRequests);				// add our new fill list to the prescription object
		prescriptionRepository.save(prescription);			// write back to MongoDB


		// show the updated prescription with the most recent fill information
		model.addAttribute("message", "Prescription filled.");
		model.addAttribute("prescription", p);
		return "prescription_show";
	}

}