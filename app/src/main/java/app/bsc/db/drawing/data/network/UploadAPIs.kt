package app.bsc.db.drawing.data.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadAPIs {
    @Multipart
    @POST("/")
    fun uploadImage(@Part file: MultipartBody.Part,  @Part("name") requestBody: RequestBody): Call<ResponseBody>
}