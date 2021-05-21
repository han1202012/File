package kim.hsl.file

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.BufferedOutputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

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

            // 查询图片文件 Uri
            queryImages()

            // 修改图片
            updateImages()

            // 删除图片文件
            deleteImages()

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
        contentValues.put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/image")
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

    /**
     * 查询图片
     */
    fun queryImages(){
        // 获取外置 SD 卡 Pictures 对应的 Uri 对象
        var externalContentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // 拼接查询语句
        var selection: String = "${MediaStore.Images.Media.DISPLAY_NAME}=?";

        // 查询语句参数
        var selectionArgs: Array<String> = arrayOf("image.jpg");

        // 查询 SQLite 数据库
        var cursor = contentResolver.query(
                // 指定要查询的 Uri
                externalContentUri,

                // 指定要查询的列
                null,

                // 指定查询语句
                selection,

                // 指定查询参数
                selectionArgs,

                // 排序规则
                null
        )

        // 先获取该图片在数据库中的 id , 然后通过 id 获取 Uri
        if (cursor != null && cursor.moveToFirst()){
            // 获取第 0 行 _id 所在列的值
            var id = cursor.getLong(
                    // 获取 _id 所在列的索引
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            )

            var path = cursor.getString(
                    // 获取 relative_path 所在列的索引
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
            )

            var name = cursor.getString(
                    // 获取 _display_name 所在列的索引
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            )

            // 绝对路径
            var absolutePath = cursor.getString(
                    // 获取 data 所在列的索引
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            )

            // 通过 _id 字段获取图片 Uri
            var uri = ContentUris.withAppendedId(externalContentUri, id);

            Log.i(TAG, "查询到的 Uri = $uri , 路径 = $path , 文件名称 = $name , 绝对路径 = $absolutePath")

            // 关闭游标
            cursor.close()
        }
    }

    /**
     * 修改图片
     */
    fun updateImages(){
        // 要删除的图片对应的 Uri, 需要先查询出来
        var uri: Uri?= null

        // 查询 SQLite 数据库
        var cursor = contentResolver.query(
                // 指定要查询的 Uri
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                // 指定要查询的列
                null,
                // 指定查询语句
                "${MediaStore.Images.Media.DISPLAY_NAME}=?",
                // 指定查询参数
                arrayOf("image.jpg"),
                // 排序规则
                null
        )

        // 先获取该图片在数据库中的 id , 然后通过 id 获取 Uri
        if (cursor != null && cursor.moveToFirst()){
            // 获取第 0 行 _id 所在列的值
            var id = cursor.getLong(
                    // 获取 _id 所在列的索引
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            )
            // 通过 _id 字段获取图片 Uri
            uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            Log.i(TAG, "查询到的 Uri = $uri , 开始准备修改")
            // 关闭游标
            cursor.close()
        }

        // 修改图片

        // 构造 ContentValues
        var contentValues: ContentValues = ContentValues();
        // 将 display_name 修改成 image_update
        contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, "image_update.jpg")

        // 修改文件名称
        var row = contentResolver.update(uri!!, contentValues, null, null)

        Log.i(TAG, "修改 uri = $uri 结果 row = $row")
    }

    /**
     * 删除图片
     */
    fun deleteImages(){
        // 要删除的图片对应的 Uri, 需要先查询出来
        var uri: Uri?= null

        // 查询 SQLite 数据库
        var cursor = contentResolver.query(
                // 指定要查询的 Uri
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                // 指定要查询的列
                null,
                // 指定查询语句
                "${MediaStore.Images.Media.DISPLAY_NAME}=?",
                // 指定查询参数
                arrayOf("image_update.jpg"),
                // 排序规则
                null
        )

        // 先获取该图片在数据库中的 id , 然后通过 id 获取 Uri
        if (cursor != null && cursor.moveToFirst()){
            // 获取第 0 行 _id 所在列的值
            var id = cursor.getLong(
                    // 获取 _id 所在列的索引
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            )
            // 通过 _id 字段获取图片 Uri
            uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            Log.i(TAG, "查询到的 Uri = $uri , 开始准备删除")
            // 关闭游标
            cursor.close()
        }

        // 删除图片
        var row = contentResolver.delete(uri!!, null, null)

        Log.i(TAG, "删除 uri = $uri 结果 row = $row")
    }

}