package com.jobhelper.careerflowapi.resume.generator;

import com.jobhelper.careerflowapi.resume.domain.entity.Resume;

public interface ResumeGenerator {

    byte[] generate(Resume resume);
}
