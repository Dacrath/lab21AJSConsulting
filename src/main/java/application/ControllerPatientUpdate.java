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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import view.*;
/*
 * Controller class for patient interactions.
 *   register as a new patient.
 *   update patient profile.
 */
@Controller
public class ControllerPatientUpdate {

	@Autowired
	DoctorRepository doctorRepository;

	@Autowired
	PatientRepository patientRepository;

	@Autowired
	SequenceService sequence;

	/*
	 *  Display patient profile for patient id.
	 */
	@GetMapping("/patient/edit/{id}")
	public String getUpdateForm(@PathVariable int id, Model model) {

		PatientView pv = new PatientView();


		// model.addAttribute("message", some message);
		// model.addAttribute("patient", pv
		// return editable form with patient data
		Patient patient = patientRepository.findById(id);

		if(patient != null) {

				pv.setId(id);
				pv.setFirstName(patient.getFirstName());
				pv.setLastName(patient.getLastName());
				pv.setBirthdate(patient.getBirthdate());
				pv.setStreet(patient.getStreet());
				pv.setCity(patient.getCity());
				pv.setState(patient.getState());
				pv.setZipcode(patient.getZipcode());
				pv.setPrimaryName(patient.getPrimaryName());

				model.addAttribute("patient", pv);
				return "patient_edit";
			}
			else{
				model.addAttribute("message", "Patient not found.");
				System.out.println(pv.getId());
				return "index";
			}

}
	
	
	/*
	 * Process changes from patient_edit form
	 *  Primary doctor, street, city, state, zip can be changed
	 *  ssn, patient id, name, birthdate, ssn are read only in template.
	 */
	@PostMapping("/patient/edit")
	public String updatePatient(PatientView p, Model model) {

		/*
		 * validate doctor last name and find the doctor id
		 */
		//
		Doctor doctor = doctorRepository.findByLastName(p.getPrimaryName());

		if (doctor == null) {
			model.addAttribute("message", "Doctor not found, check spelling of last name.");
			model.addAttribute("patient", p);
			return "patient_edit";
		}


		Patient updatePatient = patientRepository.findById(p.getId());
		updatePatient.setStreet(p.getStreet());
		updatePatient.setCity(p.getCity());
		updatePatient.setState(p.getState());
		updatePatient.setZipcode(p.getZipcode());
		updatePatient.setPrimaryName(doctor.getLastName());

		patientRepository.save(updatePatient);

		model.addAttribute("message", "Patient information updated.");
		model.addAttribute("patient", p);
		return "patient_show";
//		}
	}

}
