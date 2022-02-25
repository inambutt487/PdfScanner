package me.sid.smartcropper.utils

import me.sid.smartcropper.models.FileInfoModel

class FileListStatic {
     companion object{
         @JvmStatic var mFiles=ArrayList<FileInfoModel>()
         @JvmStatic var count=0
         @JvmStatic  fun addFile(file:FileInfoModel){
            mFiles.add(file)
        }
       @JvmStatic fun increment(){
           count= count+1
       }
         @JvmStatic  fun setFiles(files:ArrayList<FileInfoModel>){
            mFiles=files
        }
    }
}