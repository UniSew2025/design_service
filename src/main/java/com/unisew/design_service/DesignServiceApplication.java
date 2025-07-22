package com.unisew.design_service;

import com.unisew.design_service.enums.ClothCategory;
import com.unisew.design_service.enums.ClothType;
import com.unisew.design_service.enums.Fabric;
import com.unisew.design_service.enums.Gender;
import com.unisew.design_service.enums.Status;
import com.unisew.design_service.models.*;
import com.unisew.design_service.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
@EnableFeignClients
public class DesignServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesignServiceApplication.class, args);
    }
    @Bean
    public CommandLineRunner initData(
            DesignRequestRepo designRequestRepo,
            ClothRepo clothRepo,
            DesignDeliveryRepo designDeliveryRepo,
            FinalImageRepo finalImageRepo,
            RevisionRequestRepo revisionRequestRepo,
            SampleImageRepo sampleImageRepo,
            DesignCommentRepo designCommentRepo) {
        return args -> {
            // ======== DESIGN REQUESTS ========
            DesignRequest request1 = DesignRequest.builder()
                    .creationDate(LocalDate.now().minusDays(10))
                    .isPrivate(true)
                    .status(Status.COMPLETED)
                    .schoolId(5)
                    .packageId(1)
                    .packageName("Basic Design")
                    .packagePrice(1000000)
                    .packageDeliveryDate(5)
                    .revisionTime(2)
                    .feedbackId(0)
                    .build();

            DesignRequest request2 = DesignRequest.builder()
                    .creationDate(LocalDate.now().minusDays(7))
                    .isPrivate(false)
                    .status(Status.PAID)
                    .schoolId(5)
                    .packageId(1)
                    .packageName("Basic Design")
                    .packagePrice(1000000)
                    .packageDeliveryDate(5)
                    .revisionTime(2)
                    .feedbackId(0)
                    .build();

            DesignRequest request3 = DesignRequest.builder()
                    .creationDate(LocalDate.now().minusDays(10))
                    .isPrivate(true)
                    .status(Status.COMPLETED)
                    .schoolId(5)
                    .packageId(1)
                    .packageName("Basic Design")
                    .packagePrice(1000000)
                    .packageDeliveryDate(5)
                    .revisionTime(2)
                    .feedbackId(0)
                    .build();

            designRequestRepo.saveAll(List.of(request1, request2, request3));

            // ======== CLOTHS ========
            Cloth boyShirt = Cloth.builder()
                    .type(ClothType.PANTS)
                    .category(ClothCategory.REGULAR)
                    .color("BLack")
                    .fabric(Fabric.COTTON)
                    .gender(Gender.BOY)
                    .note("Short sleeve white shirt for boys")
                    .logoImage("https://res.cloudinary.com/di1aqthok/image/upload/v1750783862/yaepvqd7lvcze1rdyght.jpg")
                    .designRequest(request1)
                    .build();

            Cloth girlSkirt = Cloth.builder()
                    .type(ClothType.SHIRT)
                    .category(ClothCategory.REGULAR)
                    .color("White")
                    .fabric(Fabric.POLYESTER)
                    .gender(Gender.BOY)
                    .note("Elementary school uniform skirts for girls")
                    .logoImage("https://res.cloudinary.com/di1aqthok/image/upload/v1750783862/yaepvqd7lvcze1rdyght.jpg")
                    .logoPosition("Front belt")
                    .logoWidth(60)
                    .logoHeight(50)
                    .designRequest(request1)
                    .build();

            Cloth pePants = Cloth.builder()
                    .type(ClothType.PANTS)
                    .category(ClothCategory.PHYSICAL)
                    .color("Dark gray")
                    .fabric(Fabric.SILK)
                    .gender(Gender.BOY)
                    .note("Educate pants for male students")
                    .logoImage("https://res.cloudinary.com/di1aqthok/image/upload/v1750783862/yaepvqd7lvcze1rdyght.jpg")
                    .designRequest(request2)
                    .build();

            Cloth peShirt = Cloth.builder()
                    .type(ClothType.SHIRT)
                    .category(ClothCategory.PHYSICAL)
                    .color("White with blue")
                    .fabric(Fabric.DENIM)
                    .gender(Gender.BOY)
                    .logoImage("https://res.cloudinary.com/di1aqthok/image/upload/v1750783862/yaepvqd7lvcze1rdyght.jpg")
                    .note("Gym shirt for female students")
                    .designRequest(request2)
                    .build();

            Cloth regularBoyShirt = Cloth
                    .builder()
                    .logoHeight(100)
                    .logoWidth(100)
                    .designRequest(request3)
                    .template(null)
                    .category(ClothCategory.REGULAR)
                    .gender(Gender.BOY)
                    .type(ClothType.SHIRT)
                    .color("#FFFFFF")
                    .fabric(Fabric.SILK)
                    .logoImage("https://res.cloudinary.com/di1aqthok/image/upload/v1750783862/yaepvqd7lvcze1rdyght.jpg")
                    .logoPosition("top-left")
                    .note("Regular boy shirt for primary students")
                    .build();

            Cloth regularBoyPant = Cloth
                    .builder()
                    .designRequest(request3)
                    .category(ClothCategory.REGULAR)
                    .gender(Gender.BOY)
                    .type(ClothType.PANTS)
                    .color("#000000")
                    .fabric(Fabric.JEAN)
                    .note("Regular boy pant for primary students")
                    .build();

            Cloth regularGirlShirt = Cloth
                    .builder()
                    .logoHeight(100)
                    .logoWidth(100)
                    .designRequest(request3)
                    .category(ClothCategory.REGULAR)
                    .gender(Gender.GIRL)
                    .type(ClothType.SHIRT)
                    .color("#FFFFFF")
                    .fabric(Fabric.SILK)
                    .logoImage("https://res.cloudinary.com/di1aqthok/image/upload/v1750783862/yaepvqd7lvcze1rdyght.jpg")
                    .logoPosition("top-left")
                    .note("Regular girl shirt for primary students")
                    .build();
            Cloth regularGirlSkirt = Cloth
                    .builder()
                    .designRequest(request3)
                    .category(ClothCategory.REGULAR)
                    .gender(Gender.GIRL)
                    .type(ClothType.SKIRT)
                    .color("#000000")
                    .fabric(Fabric.SILK)
                    .note("Regular girt skirt for primary students")
                    .build();
            Cloth boyPEShirt = Cloth
                    .builder()
                    .logoHeight(200)
                    .logoWidth(200)
                    .designRequest(request3)
                    .template(null)
                    .category(ClothCategory.PHYSICAL)
                    .gender(Gender.BOY)
                    .type(ClothType.SHIRT)
                    .color("#f7d308")
                    .fabric(Fabric.SILK)
                    .logoImage("https://res.cloudinary.com/di1aqthok/image/upload/v1750783862/yaepvqd7lvcze1rdyght.jpg")
                    .logoPosition("back")
                    .note("PE boy shirt for primary students")
                    .build();
            Cloth boyPEPant = Cloth
                    .builder()
                    .designRequest(request3)
                    .category(ClothCategory.PHYSICAL)
                    .gender(Gender.BOY)
                    .type(ClothType.PANTS)
                    .color("#2c3e50")
                    .fabric(Fabric.SILK)
                    .note("PE boy pant for primary students")
                    .build();
            Cloth girlPEShirt = Cloth
                    .builder()
                    .logoHeight(200)
                    .logoWidth(200)
                    .designRequest(request3)
                    .template(null)
                    .category(ClothCategory.PHYSICAL)
                    .gender(Gender.GIRL)
                    .type(ClothType.SHIRT)
                    .color("#f7d308")
                    .fabric(Fabric.SILK)
                    .logoImage("https://res.cloudinary.com/di1aqthok/image/upload/v1750783862/yaepvqd7lvcze1rdyght.jpg")
                    .logoPosition("back")
                    .note("PE boy shirt for primary students")
                    .build();
            Cloth girlPEPant = Cloth
                    .builder()
                    .designRequest(request3)
                    .category(ClothCategory.PHYSICAL)
                    .gender(Gender.GIRL)
                    .type(ClothType.PANTS)
                    .color("#2c3e50")
                    .fabric(Fabric.SILK)
                    .note("PE girl pant for primary students")
                    .build();
            clothRepo.saveAll(List.of(
                    boyShirt, girlSkirt, pePants, peShirt,
                    regularBoyShirt, regularBoyPant, regularGirlShirt, regularGirlSkirt,
                    boyPEShirt, boyPEPant, girlPEShirt, girlPEPant
            ));

            // ======== DESIGN DELIVERY (each request has a delivery) ========
            DesignDelivery delivery1 = DesignDelivery.builder()
                    .designRequest(request1)
                    .fileUrl("https://cdn.school.com/request1_delivery1.zip")
                    .deliveryNumber(1)
                    .submitDate(LocalDateTime.now().minusDays(8))
                    .isFinal(true)
                    .isRevision(true)
                    .note("Initial delivery for request1")
                    .build();

            DesignDelivery delivery2 = DesignDelivery.builder()
                    .designRequest(request2)
                    .fileUrl("https://cdn.school.com/request2_delivery1.zip")
                    .deliveryNumber(1)
                    .submitDate(LocalDateTime.now().minusDays(5))
                    .isFinal(false)
                    .isRevision(false)
                    .note("First version for request2")
                    .build();
            DesignDelivery delivery3 = DesignDelivery.builder()
                    .designRequest(request3)
                    .fileUrl("https://cdn.school.com/request2_delivery1.zip")
                    .deliveryNumber(1)
                    .submitDate(LocalDateTime.now().minusDays(5))
                    .isFinal(true)
                    .isRevision(true)
                    .note("First version for request3")
                    .build();

            designDeliveryRepo.saveAll(List.of(delivery1, delivery2, delivery3));

            // ======== REVISION REQUESTS (attached to delivery) ========
            RevisionRequest rev1 = RevisionRequest.builder()
                    .note("Please add front buttons to shirt design")
                    .createdAt(LocalDate.now().minusDays(7))
                    .delivery(delivery1)
                    .build();

            RevisionRequest rev2 = RevisionRequest.builder()
                    .note("Logo is too small, make it bigger")
                    .createdAt(LocalDate.now().minusDays(4))
                    .delivery(delivery2)
                    .build();

            revisionRequestRepo.saveAll(List.of(rev1, rev2));

            // ======== FINAL IMAGES (attached to cloth) ========
            FinalImage image1 = FinalImage.builder()
                    .name("Men's shirt front")
                    .imageUrl("https://cdn.school.com/final_boy_shirt_front.png")
                    .cloth(boyShirt)
                    .build();

            FinalImage image2 = FinalImage.builder()
                    .name("Girl skirt final")
                    .imageUrl("https://cdn.school.com/final_girl_skirt.png")
                    .cloth(girlSkirt)
                    .build();

            FinalImage image3 = FinalImage.builder()
                    .name("PE pants")
                    .imageUrl("https://cdn.school.com/final_pe_pants.png")
                    .cloth(pePants)
                    .build();

            FinalImage image4 = FinalImage.builder()
                    .name("PE shirt")
                    .imageUrl("https://cdn.school.com/final_pe_shirt.png")
                    .cloth(peShirt)
                    .build();
            FinalImage imageRegularBoyShirt = FinalImage.builder()
                    .name("Regular boy shirt")
                    .imageUrl("https://cdn.school.com/final_pe_shirt.png")
                    .cloth(regularBoyShirt)
                    .build();
            FinalImage imageRegularBoyPant = FinalImage.builder()
                    .name("Regular boy pant")
                    .imageUrl("https://cdn.school.com/final_pe_shirt.png")
                    .cloth(regularBoyPant)
                    .build();
            FinalImage imageRegularGirlShirt = FinalImage.builder()
                    .name("Regular girl shirt")
                    .imageUrl("https://cdn.school.com/final_pe_shirt.png")
                    .cloth(regularGirlShirt)
                    .build();
            FinalImage imageRegularGirlSkirt = FinalImage.builder()
                    .name("Regular girl skirt")
                    .imageUrl("https://cdn.school.com/final_pe_shirt.png")
                    .cloth(regularGirlSkirt)
                    .build();
            FinalImage imagePEBoyShirt = FinalImage.builder()
                    .name("PE boy shirt")
                    .imageUrl("https://cdn.school.com/final_pe_shirt.png")
                    .cloth(boyPEShirt)
                    .build();
            FinalImage imagePEBoyPant = FinalImage.builder()
                    .name("PE boy pant")
                    .imageUrl("https://cdn.school.com/final_pe_shirt.png")
                    .cloth(boyPEPant)
                    .build();
            FinalImage imagePEGirlShirt = FinalImage.builder()
                    .name("PE girl shirt")
                    .imageUrl("https://cdn.school.com/final_pe_shirt.png")
                    .cloth(girlPEShirt)
                    .build();
            FinalImage imagePEGirlPant = FinalImage.builder()
                    .name("PE girl pant")
                    .imageUrl("https://cdn.school.com/final_pe_shirt.png")
                    .cloth(girlPEPant)
                    .build();



            finalImageRepo.saveAll(List.of(image1, image2, image3,
                    imageRegularBoyShirt,imageRegularBoyPant,imageRegularGirlShirt,imageRegularGirlSkirt,
                    imagePEBoyShirt,imagePEBoyPant,imagePEGirlShirt,imagePEGirlPant));

            // ======== SAMPLE IMAGES ========
            String cloudinarySampleUrl = "https://res.cloudinary.com/di1aqthok/image/upload/v1750703944/onmapboetu92mv4hvxz0.jpg";

            SampleImage sample1 = SampleImage.builder()
                    .imageUrl(cloudinarySampleUrl)
                    .cloth(boyShirt)
                    .build();

            SampleImage sample2 = SampleImage.builder()
                    .imageUrl(cloudinarySampleUrl)
                    .cloth(girlSkirt)
                    .build();

            SampleImage sample3 = SampleImage.builder()
                    .imageUrl(cloudinarySampleUrl)
                    .cloth(pePants)
                    .build();

            SampleImage sample4 = SampleImage.builder()
                    .imageUrl(cloudinarySampleUrl)
                    .cloth(regularBoyShirt)
                    .build();

            SampleImage sample5 = SampleImage.builder()
                    .imageUrl(cloudinarySampleUrl)
                    .cloth(regularBoyPant)
                    .build();

            SampleImage sample6 = SampleImage.builder()
                    .imageUrl(cloudinarySampleUrl)
                    .cloth(regularGirlShirt)
                    .build();
            SampleImage sample7 = SampleImage.builder()
                    .imageUrl(cloudinarySampleUrl)
                    .cloth(regularGirlSkirt)
                    .build();
            SampleImage sample8 = SampleImage.builder()
                    .imageUrl(cloudinarySampleUrl)
                    .cloth(boyPEShirt)
                    .build();

            SampleImage sample9 = SampleImage.builder()
                    .imageUrl(cloudinarySampleUrl)
                    .cloth(boyPEPant)
                    .build();

            SampleImage sample10 = SampleImage.builder()
                    .imageUrl(cloudinarySampleUrl)
                    .cloth(girlPEShirt)
                    .build();
            SampleImage sample11 = SampleImage.builder()
                    .imageUrl(cloudinarySampleUrl)
                    .cloth(girlPEPant)
                    .build();


            sampleImageRepo.saveAll(List.of(sample1, sample2, sample3,sample4,sample5,sample6,sample7,sample8,sample9,sample10,sample11));

            // ======== COMMENTS ========
            DesignComment comment1 = DesignComment.builder()
                    .designRequest(request2)
                    .senderId(1)
                    .creationDate(LocalDateTime.now())
                    .content("This is a first comment")
                    .senderRole("Designer")
                    .build();

            DesignComment comment2 = DesignComment.builder()
                    .designRequest(request2)
                    .senderId(0)
                    .creationDate(LocalDateTime.now())
                    .content("Designer submitted delivery 1")
                    .senderRole("System")
                    .build();

            DesignComment comment3 = DesignComment.builder()
                    .designRequest(request2)
                    .senderId(5)
                    .creationDate(LocalDateTime.now())
                    .content("Second comment from school")
                    .senderRole("School")
                    .build();

            designCommentRepo.saveAll(List.of(comment1, comment2, comment3));
        };
    }


}
