package com.seki.saezurishiki.application

import com.seki.saezurishiki.model.impl.ModelModule
import dagger.Component

@Component(modules = [ModelModule::class])
interface ApplicationComponent