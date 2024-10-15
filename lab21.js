// file: Lab21MockData
// Description: Populates sample data to the Lab21 MongoDB Prescription project
// Date: 2024-10-13
// Suggested testing entities: PrescriptionFill: rxid=2, patientName=Wonka, pharmacyName=DrugMart, pharmacyAddress=83 Wharehouse Lane

// 1. Doctor Data
// This is commented out as only Pharmacy and Drug are expected on final submission
//db.doctor.drop();
//db.doctor.insertMany([
//{ _id: 1, lastName: "Welsh", firstName: "Frank", specialty: "Podiatry", practiceSinceYear: 1991, ssn: "943392291"},
//{ _id: 2, lastName: "Smithe", firstName: "Richard", specialty: "Pediatrics", practiceSinceYear: 2007, ssn: "394929683"},
//{ _id: 3, lastName: "Gupta", firstName: "Sanjeet", specialty: "Cardiology", practiceSinceYear: 2001, ssn: "294829583"},
//{ _id: 4, lastName: "Arnold", firstName: "Greg", specialty: "Obstetrics", practiceSinceYear: 2011, ssn: "294835933"},
//{ _id: 5, lastName: "Urgua", firstName: "Sheila", specialty: "Anesthesiology", practiceSinceYear: 2017, ssn: "794991395"}
//]);

//print("Displaying the Doctor collection");
//cursor = db.doctor.find();
//while (cursor.hasNext()) {
//	let d = cursor.next();
//	print(d);
//}

// 2. Pharmacy Data
// Note that the mongoDB version of this includes drug costs as an array within the Pharmacy document
db.pharmacy.drop();
db.pharmacy.insertMany([
{ _id: 1, name: "DrugMart", address: "83 Wharehouse Lane", phone: "9192945631",
	drugCosts: [ { drugName: "Vanilla Beans", cost: 8.23 }, { drugName: "Chicken Soup", cost: 13.33}, { drugName: "Ranch Dressing", cost: 11.99} ] },
{ _id: 2, name: "Rx4Less", address: "www.Rx4Less.com", phone: "4593921193",
	drugCosts: [ { drugName: "Vanilla Beans", cost: 9.00 }, { drugName: "Chicken Soup", cost: 12.50 }, { drugName: "Ranch Dressing", cost: 8.99} ] },
{ _id: 3, name: "Fast Scrips", address: "59384 Whopping Way", phone: "5493921944",
	drugCosts: [ { drugName: "Vanilla Beans", cost: 9.44 }, { drugName: "Chicken Soup", cost: 11.88 }, { drugName: "Ranch Dressing", cost: 6.88} ] }
]);

// 3. Patient Data
// This is commented out as only Pharmacy and Drug are expected on final submission
//db.patient.drop();
//db.patient.insertMany([
//{ _id: 1, ssn: "592096944", firstName: "Calvin", lastName: "Yost", birthdate: "1991-04-21", street: "933 Cypress Ave.", city: "Columbus", state: "OH", zipcode: "60491", primaryName: "Welsh"},
//{ _id: 2, ssn: "958793855", firstName: "Bill", lastName: "Wiliams", birthdate: "1977-08-02", street: "693 Banglo St.", city: "Ringly", state: "KS", zipcode: "70295", primaryName: "Welsh"},
//{ _id: 3, ssn: "939387890", firstName: "Susan", lastName: "Thangle", birthdate: "1967-06-11", street: "4980 Yellow River", city: "Concrelde", state: "ID", zipcode: "89381", primaryName: "Arnold"},
//{ _id: 4, ssn: "386765283", firstName: "Vincent", lastName: "Green", birthdate: "1983-07-07", street: "693 Ivory Rd.", city: "Glasshouse", state: "OR", zipcode: "92111", primaryName: "Urgua"},
//{ _id: 5, ssn: "278619121", firstName: "Will", lastName: "Wonka", birthdate: "1961-01-01", street: "1 Chocolate Factory", city: "London", state: "AZ", zipcode: "43123", primaryName: "Smithe"}
//]);


// 4. Prescription Data
// Note that the mongoDB version of this includes fill data as an array within the prescription
// This is commented out as only Pharmacy and Drug are expected on final submission
//db.prescription.drop();
//db.prescription.insertMany([
//{ _id: 1, doctorId: 1, patientId: 2, dateCreated: "2024-07-01", drugName: "Vanilla Beans", quantity: 50, refills: 1},
//{ _id: 2, doctorId: 2, patientId: 5, dateCreated: "2023-11-15", drugName: "Chicken Soup", quantity: 3, refills: 3,
//	fills: [ { pharmacyID: 1, dateFilled: "2023-11-18", cost: "10.00" },
//			 { pharmacyID: 1, dateFilled: "2023-12-01", cost: "10.00" }
//			] },
//{ _id: 3, doctorId: 4, patientId: 4, dateCreated: "2024-10-08", drugName: "Ranch Dressing", quantity: 30, refills: 1,
//	fills: [ { pharmacyID: 2, dateFilled: "2024-10-12", cost: "12.99"}
//			] }
//]);

// 5. Drug Data
db.drug.drop();
db.drug.insertMany( [
{ _id: 880294, name: 'Vanilla Beans'},
{ _id: 93411, name: 'Chicken Soup'},
{ _id: 6938491, name: 'Ranch Dressing'}
]);

// 6. Sequence Data
// Note: the starting values below are made to work with uncommented code above for Doctor, Patient, and Prescription. 
// this is not strictly necessary if these tables are not populated with this script.
db.database_sequences.drop();
db.database_sequences.insertMany( [
{ _id: 'DOCTOR_SEQUENCE', seq: 5 },
{ _id: 'PATIENT_SEQUENCE', seq: 5 },
{ _id: 'RXID_SEQUENCE', seq: 3 }
])