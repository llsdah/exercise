rootProject.name = "exercise"
include("algorithm")
include("zerobase-3rd")
include("zerobase-3rd:data-structure-and-algorithm")
findProject(":zerobase-3rd:data-structure-and-algorithm")?.name = "data-structure-and-algorithm"
