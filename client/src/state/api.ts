import { UserRegisterRequest } from '@/lib/validation/userRegister';
import { UserLoginRequest } from '@/types/auth';
import {
  createApi,
  fetchBaseQuery,
} from "@reduxjs/toolkit/query/react";



export const api = createApi({
  baseQuery: fetchBaseQuery({baseUrl: process.env.NEXT_PUBLIC_API_BASE_URL,credentials:'include'}),
  tagTypes: ['CourseDtos'],
  endpoints: (build) => ({
    // *******************
    // -------AUTH--------
    // *******************

    // LOGIN
    login: build.mutation({
      query: (credentials:UserLoginRequest) => ({
        url: "public/login",
        method: "POST",
        body: credentials,
      }),}),
      // Logout
    logout: build.mutation({
      query: () => ({
        url: "public/logout",
        method: "POST",
      }),}),
      // Register
      register: build.mutation({
        query: (credentials:UserRegisterRequest) => ({
          url: "public/register",
          method: "POST",
          body: credentials,
        }),}),
      
    // ******************
    // --Public Courses--
    // ******************
      
    // GET LIST OF PUBLIC DTO COURSES
    getCoursesPublic:build.query<Page<CourseDto>,{category?:string,page?:number,size?:number}>({
      query:({category,page,size})=>({
        url:"public/courses",
        params:{category,page,size}
      }),
      providesTags:(result)=>result?.content.map(course=>({type:"CourseDtos",courseId:course.id}))||[]
    }),
    //  GET PUBLIC DTO COURSE DETAIL
  getCourseDetailPublic:build.query<CourseDetailsPublicDto,{courseId:string}>({
      query:({courseId})=>`public/courses${courseId}`,
      providesTags:(_result,_error,courseId)=>[{type:"CourseDtos",courseId}]
    })

    // *******************
    // --Private Courses--
    // *******************

  })

 
});

export const {useGetCoursesPublicQuery,useGetCourseDetailPublicQuery,useLoginMutation,useLogoutMutation,useRegisterMutation} = api;
