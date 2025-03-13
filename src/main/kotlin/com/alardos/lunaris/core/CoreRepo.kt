package com.alardos.lunaris.core

import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class CoreRepo(@Autowired val jbdi: Jdbi)