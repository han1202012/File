package kim.hsl.file

import android.Manifest
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.BufferedOutputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 动态权限获取
        doSomethingWithPermissions()

    }

    @AfterPermissionGranted( 100 )
    fun doSomethingWithPermissions(){
        if(EasyPermissions.hasPermissions(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            // 分支一 : 如果有上述权限, 执行该操作
            Toast.makeText(this, "权限申请通过", Toast.LENGTH_LONG).show()

            // 创建文本文件
            createTextFile()

            // 创建图片文件
            createImageFile()

        }else{
            // 分支二 : 如果没有上述权限 , 那么申请权限
            EasyPermissions.requestPermissions(
                    this,
                    "权限申请原理对话框 : 描述申请权限的原理",
                    100,

                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    /**
     * 创建文本文件
     * 在 Download 目录下创建 hello.txt
     */
    fun createTextFile(){

        // 操作 external.db 数据库
        // 获取 Uri 路径
        var uri: Uri = MediaStore.Files.getContentUri("external")

        // 将要新建的文件的文件索引插入到 external.db 数据库中
        // 需要插入到 external.db 数据库 files 表中, 这里就需要设置一些描述信息
        var contentValues: ContentValues = ContentValues()

        // 设置插入 external.db 数据库中的 files 数据表的各个字段的值

        // 设置存储路径 , files 数据表中的对应 relative_path 字段在 MediaStore 中以常量形式定义
        contentValues.put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/hello")
        // 设置文件名称
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, "hello.txt")
        // 设置文件标题, 一般是删除后缀, 可以不设置
        contentValues.put(MediaStore.Downloads.TITLE, "hello")

        // uri 表示操作哪个数据库 , contentValues 表示要插入的数据内容
        var insert: Uri = contentResolver.insert(uri, contentValues)!!

        // 向 Download/hello/hello.txt 文件中插入数据
        var os: OutputStream = contentResolver.openOutputStream(insert)!!
        var bos = BufferedOutputStream(os)
        bos.write("Hello World".toByteArray())
        bos.close()
    }


    /**
     * 创建图片文件
     * 在 Download 目录下创建 hello.txt
     */
    fun createImageFile(){

        // 操作 external.db 数据库
        // 获取 Uri 路径
        var uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        // 将要新建的文件的文件索引插入到 external.db 数据库中
        // 需要插入到 external.db 数据库 files 表中, 这里就需要设置一些描述信息
        var contentValues: ContentValues = ContentValues()

        // 设置插入 external.db 数据库中的 files 数据表的各个字段的值

        // 设置存储路径 , files 数据表中的对应 relative_path 字段在 MediaStore 中以常量形式定义
        contentValues.put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_MOVIES}/image")
        // 设置文件名称
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, "image.jpg")
        // 设置文件标题, 一般是删除后缀, 可以不设置
        contentValues.put(MediaStore.Downloads.TITLE, "image")
        // 设置 MIME_TYPE
        contentValues.put(MediaStore.Downloads.MIME_TYPE, "image/jpg")

        // uri 表示操作哪个数据库 , contentValues 表示要插入的数据内容
        var insert: Uri = contentResolver.insert(uri, contentValues)!!

        // 向 Download/hello/hello.jpg 文件中插入数据
        var os: OutputStream = contentResolver.openOutputStream(insert)!!
        var bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.icon)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.close()
    }

}