package com.evginozan.medireminder.di

import com.evginozan.medireminder.data.local.database.MedicineDatabase
import com.evginozan.medireminder.data.repository.BloodPressureRepositoryImpl
import com.evginozan.medireminder.data.repository.BloodSugarRepositoryImpl
import com.evginozan.medireminder.data.repository.MedicineRepositoryImpl
import com.evginozan.medireminder.domain.repository.BloodPressureRepository
import com.evginozan.medireminder.domain.repository.BloodSugarRepository
import com.evginozan.medireminder.domain.repository.MedicineRepository
import com.evginozan.medireminder.domain.usecase.AddBloodPressureRecordUseCase
import com.evginozan.medireminder.domain.usecase.AddBloodSugarRecordUseCase
import com.evginozan.medireminder.domain.usecase.AddMedicineUseCase
import com.evginozan.medireminder.domain.usecase.DeleteBloodPressureRecordUseCase
import com.evginozan.medireminder.domain.usecase.DeleteBloodSugarRecordUseCase
import com.evginozan.medireminder.domain.usecase.DeleteMedicineUseCase
import com.evginozan.medireminder.domain.usecase.GetAllBloodPressureRecordsUseCase
import com.evginozan.medireminder.domain.usecase.GetAllBloodSugarRecordsUseCase
import com.evginozan.medireminder.domain.usecase.GetAllMedicinesUseCase
import com.evginozan.medireminder.domain.usecase.GetBloodPressureRecordByIdUseCase
import com.evginozan.medireminder.domain.usecase.GetBloodSugarRecordByIdUseCase
import com.evginozan.medireminder.domain.usecase.GetMedicineByIdUseCase
import com.evginozan.medireminder.domain.usecase.UpdateBloodPressureRecordUseCase
import com.evginozan.medireminder.domain.usecase.UpdateBloodSugarRecordUseCase
import com.evginozan.medireminder.domain.usecase.UpdateMedicineUseCase
import com.evginozan.medireminder.presentation.bloodpressure.BloodPressureViewModel
import com.evginozan.medireminder.presentation.bloodsugar.BloodSugarViewModel
import com.evginozan.medireminder.presentation.detail.MedicineDetailViewModel
import com.evginozan.medireminder.presentation.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single { MedicineDatabase.getInstance(androidContext()) }
    single { get<MedicineDatabase>().medicineDao() }
    single { get<MedicineDatabase>().bloodPressureDao() }
    single { get<MedicineDatabase>().bloodSugarDao() }
}

val repositoryModule = module {
    single<MedicineRepository> { MedicineRepositoryImpl(get()) }
    single<BloodPressureRepository> { BloodPressureRepositoryImpl(get()) }
    single<BloodSugarRepository> { BloodSugarRepositoryImpl(get()) }
}

val useCaseModule = module {
    // Medicine UseCases
    single { GetAllMedicinesUseCase(get()) }
    single { AddMedicineUseCase(get()) }
    single { DeleteMedicineUseCase(get()) }
    single { UpdateMedicineUseCase(get()) }
    single { GetMedicineByIdUseCase(get()) }

    // Blood Pressure UseCases
    single { GetAllBloodPressureRecordsUseCase(get()) }
    single { AddBloodPressureRecordUseCase(get()) }
    single { UpdateBloodPressureRecordUseCase(get()) }
    single { DeleteBloodPressureRecordUseCase(get()) }
    single { GetBloodPressureRecordByIdUseCase(get()) }

    // Blood Sugar UseCases
    single { GetAllBloodSugarRecordsUseCase(get()) }
    single { AddBloodSugarRecordUseCase(get()) }
    single { UpdateBloodSugarRecordUseCase(get()) }
    single { DeleteBloodSugarRecordUseCase(get()) }
    single { GetBloodSugarRecordByIdUseCase(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { MedicineDetailViewModel(get(), get(), get()) }
    viewModel { BloodPressureViewModel(get(), get(), get(), get()) }
    viewModel { BloodSugarViewModel(get(), get(), get(), get()) }
}

val appModules = listOf(
    databaseModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)