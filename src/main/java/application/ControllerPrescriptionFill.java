package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import application.model.*;
import application.service.*;
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

		// validate pharmacy name and address, get pharmacy id and phone
		Pharmacy ph = pharmacyRepository.findByNameAndAddress(p.getPharmacyName(), p.getPharmacyAddress());
		if (ph != null) {
			// Pharmacy found, copy any fields needed for the final form/page
			p.setPharmacyID(ph.getId());
			p.setPharmacyName(ph.getName());
			p.setPharmacyAddress(ph.getAddress());
			p.setPharmacyPhone(ph.getPhone());
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
			// Create an array to traverse any existing fill requests
			ArrayList<Prescription.FillRequest> fillRequests = prescription.getFills();
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

		
		// save updated prescription


		// show the updated prescription with the most recent fill information
		model.addAttribute("message", "Prescription filled. Count: " + refillCount);
		model.addAttribute("prescription", p);
		return "prescription_show";
	}

}