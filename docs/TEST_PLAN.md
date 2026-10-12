# Test Plan: Live Site QA Testing

## 1. Goal
Check that the live **Women in Tech Series 2026** landing page works for visitors on desktop and mobile,
find and document defects, and keep checking it every day so new problems are caught early.

**Application under test:** https://uninetworkgroup.com/women-in-tech-series-2026/
(WordPress + Elementor, custom registration popup using Web3Forms).

## 2. Scope

**In scope:** page load, navigation, registration popup and form validation, FAQ, mobile menu,
speaker cards, broken links, image alt text (accessibility), meta description (SEO), content consistency.

**Out of scope:** sending real registrations, load testing, security testing, other pages of the site
(only their links are checked).

## 3. Approach

1. **Exploratory testing** in the browser to learn the page and spot problems.
2. **Automate** the important user paths as Cucumber scenarios.
3. **Add quality checks** (links, accessibility, SEO, content) that run across the whole page.
4. **Log every defect** in [BUG_REPORTS.md](BUG_REPORTS.md) with a linked `@known-bug` test.

### Live-site safety rules
- Tests never submit valid data: `RegistrationModal` throws an error if asked to submit a valid form.
- No accounts, comments or registrations are created.
- Link checks use `HEAD` requests to keep load on the server minimal.

## 4. Test types

| Type | Tag | When it runs | Purpose |
|---|---|---|---|
| Smoke | `@smoke` | Every push + daily | Is the page up and can visitors open the registration form? |
| Regression | `@regression` | Every push + daily | Every feature still works |
| Known bugs | `@known-bug` | On demand | Proves an open bug still exists; passes once it's fixed |

## 5. Environment
- Browser: Chrome (latest), headless in CI.
- Screen sizes: desktop 1920×1080, phone 390×844.
- CI: GitHub Actions on Ubuntu, on every push and once a day.

## 6. Test cases

| ID | User story | Test case | Type | Result |
|---|---|---|---|---|
| TC-01 | US-01 Visit | Page loads with the right title and heading | Smoke | ✅ Pass |
| TC-02 | US-02 Register | "Reserve Seat" opens the form with the cursor in Full Name | Smoke | ✅ Pass |
| TC-03 | US-01 Visit | All 6 top-menu links exist and jump to a real section | Regression | ✅ Pass |
| TC-04 | US-02 Register | Form closes with the × button | Regression | ✅ Pass |
| TC-05 | US-02 Register | Form closes with the Escape key | Regression | ✅ Pass |
| TC-06 | US-02 Register | Missing name is blocked; nothing is sent | Regression | ✅ Pass |
| TC-07 | US-02 Register | Missing email is blocked; nothing is sent | Regression | ✅ Pass |
| TC-08 | US-02 Register | Invalid email is blocked; nothing is sent | Regression | ✅ Pass |
| TC-09 | US-02 Register | "I identify as" dropdown lists all 7 options | Regression | ✅ Pass |
| TC-10 | US-03 Answers | FAQ answer opens and closes | Regression | ✅ Pass |
| TC-11 | US-04 Mobile | Mobile menu opens and closes on a phone-sized screen | Regression | ✅ Pass |
| TC-12 | US-01 Visit | Every speaker photo is labelled with the speaker's name | Regression | ✅ Pass |
| TC-13 | Quality | No internal link is broken | Known bug | ❌ BUG-001 |
| TC-14 | Content | Countdown doesn't say LIVE after the event | Known bug | ❌ BUG-002 |
| TC-15 | Content | Session length is the same everywhere | Known bug | ❌ BUG-003 |
| TC-16 | Accessibility | Every image has alt text | Known bug | ❌ BUG-004 |
| TC-17 | SEO | Meta description is a real summary (50+ characters) | Known bug | ❌ BUG-005 |
| TC-18 | US-01 Visit | Every speaker card links to LinkedIn | Known bug | ❌ BUG-007 |
| TC-19 | Content | URLs are spelled correctly | Manual | ❌ BUG-006 |

### User stories
- **US-01** As a visitor, I want the page to load and show clear event information so I can decide whether to attend.
- **US-02** As a visitor, I want to register easily and be told if I made a mistake so my registration is correct.
- **US-03** As a visitor, I want quick answers to common questions.
- **US-04** As a phone user, I want to navigate the page easily on a small screen.

## 7. Risks
- **The site is edited often.** A redesign may need locator updates; they're all in the two page classes.
- **Event-specific content.** Dates and the countdown change per edition; the date check takes the date as a parameter.
