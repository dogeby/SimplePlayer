package com.yang.simpleplayer.models

import java.util.*

data class Folder(val name:String, val videoFiles: TreeSet<Video> = TreeSet(compareBy { it.name.lowercase() }))
