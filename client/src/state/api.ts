import {
  createApi,
  fetchBaseQuery,
} from "@reduxjs/toolkit/query/react";



export const api = createApi({
  baseQuery: fetchBaseQuery({baseUrl: process.env.NEXT_PUBLIC_API_BASE_URL}),
  reducerPath: "api",
  tagTypes: ['CourseDtos'],
  endpoints: (build) => ({
    getCoursesPublic:build.query<CourseDto[],{category?:string}>({
      query:({category})=>({
        url:"public/courses",
        params:{
          category
        }
      }),
      providesTags:["CourseDtos"]
    })
  })
   

 
});

export const {} = api;
