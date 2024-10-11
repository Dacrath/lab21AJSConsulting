package application;

import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.Patient;
import application.model.PatientRepository;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import view.*;

/*
 * Controller class for patient interactions.
 *   register as a new patient.
 *   update patient profile.
 */
@Controller
public class ControllerPatientCreate {
	static final String PATIENT_SEQUENCE = "PATIENT_SEQUENCE";

	@Autowired
	DoctorRepository doctorRepository;

	@Autowired
	PatientRepository patientRepository;

	@Autowired
	SequenceService sequence;
	/*
	 * Request blank patient registration form.
	 */
	@GetMapping("/patient/new")
	public String getNewPatientForm(Model model) {
		// return blank form for new patient registration
		model.addAttribute("patient", new PatientView());
		return "patient_register";
	}
	
	/*
	 * Process data from the patient_register form
	 */
	@PostMapping("/patient/new")
	public String createPatient(PatientView p, Model model) {

		/*
		 * validate doctor last name and find the doctor id
		 */
		//
		Doctor doctor = doctorRepository.findByLastName(p.getPrimaryName());

		if (doctor == null) {
			model.addAttribute("message", "Doctor not found, check spelling of last name.");
			model.addAttribute("patient", p);
			return "patient_register";
		}


		Patient newPatient = new Patient();
		newPatient.setSsn(p.getSsn());
		newPatient.setFirstName(p.getFirstName());
		newPatient.setLastName(p.getLastName());
		newPatient.setBirthdate(p.getBirthdate());
		newPatient.setStreet(p.getStreet());
		newPatient.setCity(p.getCity());
		newPatient.setState(p.getState());
		newPatient.setZipcode(p.getZipcode());
		newPatient.setPrimaryName(doctor.getLastName());

		//Data validation
		if (newPatient.getLastName().isEmpty()||newPatient.getFirstName().isEmpty()) {
			model.addAttribute("message", "Please enter your first and last name, both are required.");
			model.addAttribute("patient", p);
			return "patient_register";
		}

		//SSN check
		Patient ssnAlreadyUsedPatient = patientRepository.findBySsn(newPatient.getSsn());
		if (newPatient.getSsn().isEmpty() || newPatient.getSsn().length() != 9 || ssnAlreadyUsedPatient != null) {
			model.addAttribute("message", "SSN is either in use, malformed or missing, please check for accuracy.  It should be in ######### format");
			model.addAttribute("patient", p);
			return "patient_register";
		}




		//Get and store new ID
		int id = sequence.getNextSequence(PATIENT_SEQUENCE);
		newPatient.setId(id);
		patientRepository.insert(newPatient);
		p.setId(id);

		// display patient data and the generated patient ID,  and success message
		model.addAttribute("message", "Registration successful.");
		model.addAttribute("patient", p);
		return "patient_show";


	}
	
	/*
	 * Request blank form to search for patient by id and name
	 */
	@GetMapping("/patient/edit")
	public String getSearchForm(Model model) {
		model.addAttribute("patient", new PatientView());
		return "patient_get";
	}
	
	/*
	 * Perform search for patient by patient id and name.
	 */
	@PostMapping("/patient/show")
	public String showPatient(PatientView p, Model model) {

	Patient patient = patientRepository.findByIdAndLastName(p.getId(), p.getLastName());
	if (patient == null) {
		model.addAttribute("message", "Patient not found, check spelling of last name and ID.");
		model.addAttribute("patient", p);
		return "patient_get";
	} else {
		p.setSsn(patient.getSsn());
		p.setFirstName(patient.getFirstName());
		p.setLastName(patient.getLastName());
		p.setBirthdate(patient.getBirthdate());
		p.setStreet(patient.getStreet());
		p.setCity(patient.getCity());
		p.setState(patient.getState());
		p.setZipcode(patient.getZipcode());
		p.setPrimaryName(patient.getPrimaryName());

		model.addAttribute("message", "Current Patient Information.");
		model.addAttribute("patient", p);
		System.out.println("end getPatient " + p);  // debug
		return "patient_show";
	}
	}
}
