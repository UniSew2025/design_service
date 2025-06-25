package com.unisew.design_service;

import com.unisew.design_service.enums.ClothCategory;
import com.unisew.design_service.enums.ClothType;
import com.unisew.design_service.enums.Status;
import com.unisew.design_service.models.*;
import com.unisew.design_service.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class DesignServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesignServiceApplication.class, args);
    }
    @Bean
    public CommandLineRunner initData(
            DesignRequestRepo designRequestRepo,
            ClothRepo clothRepo,
            DesignDraftRepo designDraftRepo,
            DraftImageRepo draftImageRepo,
            RevisionRequestRepo revisionRequestRepo,
            SampleImageRepo sampleImageRepo
    ) {
        return args -> {
            // ======== DESIGN REQUESTS ========
            DesignRequest request1 = DesignRequest.builder()
                    .creationDate(LocalDate.now().minusDays(10))
                    .isPrivate(false)
                    .status(Status.CREATED)
                    .schoolId(5)
                    .packageId(0)
                    .feedbackId(0)
                    .build();

            DesignRequest request2 = DesignRequest.builder()
                    .creationDate(LocalDate.now().minusDays(7))
                    .isPrivate(false)
                    .status(Status.PENDING)
                    .schoolId(5)
                    .packageId(0)
                    .feedbackId(0)
                    .build();

            designRequestRepo.saveAll(List.of(request1, request2));

            // ======== CLOTHS ========
            Cloth boyShirt = Cloth.builder()
                    .type(ClothType.SHIRT)
                    .category(ClothCategory.REGULAR)
                    .color("White")
                    .fabric("Cotton")
                    .gender("Male")
                    .note("Short sleeve white shirt for boys")
                    .designRequest(request1)
                    .build();

            Cloth girlSkirt = Cloth.builder()
                    .type(ClothType.SKIRT)
                    .category(ClothCategory.REGULAR)
                    .color("Blue navy")
                    .fabric("Polyester")
                    .gender("Female")
                    .note("Elementary school uniform skirts for girls")
                    .logoImage("logo_primary_girl.png")
                    .logoPosition("Front belt")
                    .logoWidth(60)
                    .logoHeight(50)
                    .designRequest(request1)
                    .build();

            Cloth pePants = Cloth.builder()
                    .type(ClothType.PANTS)
                    .category(ClothCategory.PHYSICAL_EDUCATION)
                    .color("Xám đen")
                    .fabric("Elastic")
                    .gender("Male")
                    .note("Educate pants for male students")
                    .designRequest(request2)
                    .build();

            Cloth peShirt = Cloth.builder()
                    .type(ClothType.SHIRT)
                    .category(ClothCategory.PHYSICAL_EDUCATION)
                    .color("White with blue")
                    .fabric("Polyester Elastic")
                    .gender("Female")
                    .note("Gym shirt for female students")
                    .designRequest(request2)
                    .build();

            clothRepo.saveAll(List.of(boyShirt, girlSkirt, pePants, peShirt));

            // ======== DESIGN DRAFTS ========
            DesignDraft draft1 = DesignDraft.builder()
                    .description("White shirt sketch")
                    .designDate(LocalDate.now().minusDays(8))
                    .isFinal(false)
                    .cloth(boyShirt)
                    .build();

            DesignDraft draft2 = DesignDraft.builder()
                    .description("Complete version of women's dress")
                    .designDate(LocalDate.now().minusDays(5))
                    .isFinal(true)
                    .cloth(girlSkirt)
                    .build();

            designDraftRepo.saveAll(List.of(draft1, draft2));

            // ======== DRAFT IMAGES ========
            DraftImage image1 = DraftImage.builder()
                    .name("Men's shirt front")
                    .imageUrl("http://cdn.school.com/draft_boy_shirt_front.png")
                    .designDraft(draft1)
                    .build();

            DraftImage image2 = DraftImage.builder()
                    .name("Complete dress")
                    .imageUrl("http://cdn.school.com/draft_girl_skirt_final.png")
                    .designDraft(draft2)
                    .build();

            draftImageRepo.saveAll(List.of(image1, image2));

            // ======== REVISION REQUESTS ========
            RevisionRequest rev1 = RevisionRequest.builder()
                    .note("Add front buttons")
                    .designDraft(draft1)
                    .build();

            RevisionRequest rev2 = RevisionRequest.builder()
                    .note("Small Logo")
                    .designDraft(draft2)
                    .build();

            revisionRequestRepo.saveAll(List.of(rev1, rev2));

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

            sampleImageRepo.saveAll(List.of(sample1, sample2, sample3));
        };
    }

}
