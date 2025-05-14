package com.example.medicalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.medicalapp.ui.theme.MedicalAppointmentTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.medicalapp.Admin.Presentation.Doctors.AddDoctorScreen
import com.example.medicalapp.Admin.Presentation.Specialty.AddSpecialtyScreen
import com.example.medicalappointment.Admin.Presentation.Home.AdminHomeScreen
import com.example.medicalappointment.Admin.Presentation.Hospital.AddHospitalScreen
import com.example.medicalappointment.Admin.Presentation.Hospital.HomeHospitalScreen
import com.example.medicalappointment.Admin.Presentation.Specialty.HomeSpecialtyScreen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.medicalapp.presentation.BookingScreen
import com.example.medicalapp.Admin.Auth.SignInScreen
import com.example.medicalapp.Admin.Auth.SignUpScreen
import com.example.medicalapp.Admin.Presentation.Hospitals.HospitalViewModel
import com.example.medicalapp.Admin.Presentation.Doctors.DoctorViewModel
import com.example.medicalapp.BenhNhan.Presentation.HomeScreen
import com.example.medicalapp.BenhNhan.Presentation.Patient.MedicalProfileScreen
import com.example.medicalapp.BenhNhan.Presentation.Patient.PatientProfileScreen
import com.example.medicalapp.Admin.Data.Repository.DoctorRepository
import com.example.medicalapp.Admin.Presentation.Doctors.DoctorViewModelFactory
import java.time.LocalDate
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicalapp.Admin.Presentation.Doctors.HomeDoctorScreen
import com.example.medicalapp.Admin.Presentation.Patient.HomePatientScreen
import com.example.medicalappointment.Admin.Auth.AdminUserListScreen
import com.example.medicalappointment.Admin.Data.Repository.HospitalRepository
import com.example.medicalappointment.Admin.Presentation.Doctors.SeeDoctorScreen
import com.example.medicalappointment.Admin.Presentation.Hospital.HospitalViewModelFactory
import com.example.medicalappointment.Admin.Presentation.Patient.SeePatientScreen
import com.example.medicalappointment.Admin.Presentation.Hospital.EditHospitalScreen
import com.example.medicalappointment.BenhNhan.Presentation.DoctorDetailScreen
import com.example.medicalappointment.BenhNhan.Presentation.DoctorsBySpecialtyScreen
import com.example.medicalappointment.BenhNhan.Presentation.Patient.RateDoctorScreen

import com.example.medicalappointment.BenhNhan.Presentation.hospital.HospitalDetailScreen
import com.example.medicalappointment.BenhNhan.Presentation.Patient.Scheduled
import com.example.medicalappointment.BenhNhan.Presentation.booking.SuccessScreen
import com.example.medicalappointment.benhnhan.presentation.SearchResultScreen
import com.example.medicalappointment.Bacsi.Presentation.DoctorProfileScreen
import com.example.medicalappointment.Bacsi.Presentation.DoctorScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        // Setup Firebase App Check
//        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
//            PlayIntegrityAppCheckProviderFactory.getInstance()
//        )

        setContent {
            MedicalAppointmentTheme {
                val navController = rememberNavController()
                NavigationGraph(navController = navController)
            }
        }
    }
}
@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "signin"
    ) {
        // Authentication Screens
        composable("signin") { SignInScreen(navController) }
        composable("signup") { SignUpScreen(navController) }

        // Home & Profile
        composable("homescreen") { HomeScreen(navController) }
        composable("patient_profile") { MedicalProfileScreen(navController) }
        composable("patient_profilescreen") { PatientProfileScreen(navController) }
        composable ( "decentralization" ){ AdminUserListScreen(navController) }

        // Admin Screens
        composable("admin_home") {
            AdminHomeScreen(navController = navController, userName = "Admin")
        }
        composable("home_specialty") { HomeSpecialtyScreen(navController) }
        composable("add_specialty") { AddSpecialtyScreen(navController) }
        composable("home_doctor") { HomeDoctorScreen(navController) }
        composable("add_doctor") { AddDoctorScreen(navController) }
        composable("home_patient") { HomePatientScreen(navController) }
        composable("home_hospital") { HomeHospitalScreen(navController) }
        composable("add_hospital") { AddHospitalScreen(navController) }

        // Booking Screens
        composable(
            route = "bookingscreen/{doctorId}",
            arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: return@composable
            BookingScreen(navController = navController, doctorId = doctorId)
        }

        composable(
            route = "bookingconfirmscreen/{date}/{time}/{doctorId}",
            arguments = listOf(
                navArgument("date") { type = NavType.StringType },
                navArgument("time") { type = NavType.StringType },
                navArgument("doctorId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dateStr = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
            val time = backStackEntry.arguments?.getString("time") ?: "09:00"
            val date = LocalDate.parse(dateStr)
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: return@composable
            BookingConfirmScreen(navController, date, time, doctorId)
        }

        // Success
        composable("successscreen") { SuccessScreen(navController) }

        // Detail Screens
        composable("doctor_detail/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: return@composable
            val viewModel: DoctorViewModel = viewModel(
                factory = DoctorViewModelFactory(DoctorRepository())
            )
            val doctorState by viewModel.selectedDoctor.collectAsState()
            LaunchedEffect(doctorId) {
                viewModel.fetchDoctorById(doctorId)
            }

            doctorState?.let { doc ->
                DoctorDetailScreen(
                    doctor = doc,
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }
        }


        composable("hospital_detail/{hospitalId}") { backStackEntry ->
            val hospitalId = backStackEntry.arguments?.getString("hospitalId") ?: return@composable
            val viewModel: HospitalViewModel = viewModel(
                factory = HospitalViewModelFactory(HospitalRepository())
            )
            val hospitalState by viewModel.selectedHospital.collectAsState()
            LaunchedEffect(hospitalId) {
                viewModel.fetchHospitalById(hospitalId)
            }
            hospitalState?.let { hospital ->
                HospitalDetailScreen(navController = navController, hospital = hospital)
            }
        }

        composable("doctors_by_specialty/{specialtyName}") { backStackEntry ->
            val specialtyName = backStackEntry.arguments?.getString("specialtyName") ?: ""
            DoctorsBySpecialtyScreen(navController = navController, specialtyName = specialtyName)
        }

        composable("search_result/{query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchResultScreen(navController, query)
        }

        composable("scheduled/{IdBenhNhan}") { backStackEntry ->
            val idBenhNhan = backStackEntry.arguments?.getString("IdBenhNhan") ?: ""
            Scheduled(navController)

        }
        composable("see_patient/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            SeePatientScreen(navController, userId)
        }
        composable("see_doctor/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            SeeDoctorScreen(navController, doctorId)
        }
//        composable("chat_screen/{senderId}/{receiverId}") { backStackEntry ->
//            val senderId = backStackEntry.arguments?.getString("senderId") ?: return@composable
//            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: return@composable
//            ChatScreen(senderId = senderId, receiverId = receiverId)
//        }
        composable("homeDoctor/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            DoctorScreen(doctorId = doctorId, navController = navController)
        }
        composable("ratedoctor/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            RateDoctorScreen(navController = navController, doctorId = doctorId)
        }

        composable("doctorProfile/{doctorId}") { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            DoctorProfileScreen(doctorId = doctorId, navController = navController)
        }

        composable("edit_hospital/{hospitalId}") { backStackEntry ->
            val hospitalId = backStackEntry.arguments?.getString("hospitalId") ?: ""
            EditHospitalScreen(navController = navController, hospitalId = hospitalId)
        }

    }
}


