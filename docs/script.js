// Configuration constants
const MIN_STARS_THRESHOLD = 500;
// Categories to exclude from display - add more categories to this array as needed
const EXCLUDED_CATEGORIES = [
    "Educational & Learning",
    "Development Tools & SDK Management",
    "Gaming & Entertainment"];

let allRepos = [];
let filteredRepos = [];
let allCategories = [];
let allLanguages = [];

// Helper function to get the effective category (prioritizing category_corrected)
function getEffectiveCategory(repo) {
    return (repo.category_corrected && repo.category_corrected.trim() !== '')
        ? repo.category_corrected
        : repo.category;
}



// URL parameter management functions
function getUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    return {
        search: urlParams.get('search') || '',
        category: urlParams.get('category') || '',
        language: urlParams.get('language') || ''
    };
}

function updateUrlParams(search, category, language) {
    const url = new URL(window.location);

    // Remove empty parameters
    if (search) {
        url.searchParams.set('search', search);
    } else {
        url.searchParams.delete('search');
    }

    if (category) {
        url.searchParams.set('category', category);
    } else {
        url.searchParams.delete('category');
    }

    if (language) {
        url.searchParams.set('language', language);
    } else {
        url.searchParams.delete('language');
    }

    // Update URL without reloading the page
    window.history.replaceState({}, '', url);
}

function loadFiltersFromUrl() {
    const params = getUrlParams();

    // Set form values from URL parameters
    document.getElementById('searchInput').value = params.search;

    // Only set category if it exists in the available options
    const categoryFilter = document.getElementById('categoryFilter');
    if (params.category && allCategories.includes(params.category)) {
        categoryFilter.value = params.category;
    } else {
        categoryFilter.value = '';
    }

    // Only set language if it exists in the available options
    const languageFilter = document.getElementById('languageFilter');
    if (params.language && allLanguages.includes(params.language)) {
        languageFilter.value = params.language;
    } else {
        languageFilter.value = '';
    }
}

async function loadRepositories() {
    try {
        const response = await fetch('data.js');
        const repos = await response.json();

        // Filter repositories with minimum stars threshold, visibility filter, category exclusions, and sort by stars (descending)
        allRepos = repos
            .filter(repo => parseInt(repo.stars) >= MIN_STARS_THRESHOLD)
            .filter(repo => {
                // If visible field doesn't exist, show the repository (default visible)
                // If visible field exists, only show if it's not "false"
                return !repo.hasOwnProperty('visible') || repo.visible !== "false";
            })
            .filter(repo => {
                // Exclude repositories from specified categories
                const effectiveCategory = getEffectiveCategory(repo);
                return !EXCLUDED_CATEGORIES.includes(effectiveCategory);
            })
            .sort((a, b) => parseInt(b.stars) - parseInt(a.stars));
        filteredRepos = [...allRepos];

        // Extract unique categories from filtered repositories only (prioritizing category_corrected)
        allCategories = [...new Set(allRepos.map(repo => getEffectiveCategory(repo)).filter(cat => cat && cat.trim() !== ''))].sort();
        populateCategoryFilter();

        // Extract unique languages from filtered repositories only
        allLanguages = [...new Set(allRepos.map(repo => repo.language).filter(lang => lang && lang.trim() !== ''))].sort();
        populateLanguageFilter();

        // Load filters from URL and apply them
        loadFiltersFromUrl();
        filterRepositories();
        updateStats();
        updateLastReviewDate();

        document.getElementById('loading').style.display = 'none';
        document.getElementById('reposGrid').style.display = 'grid';
    } catch (error) {
        console.error('Error loading repositories:', error);
        document.getElementById('loading').innerHTML = '❌ Error loading repositories. Please check if the JSON file exists.';
    }
}

function displayRepositories() {
    const grid = document.getElementById('reposGrid');
    const noResults = document.getElementById('noResults');

    if (filteredRepos.length === 0) {
        grid.style.display = 'none';
        noResults.style.display = 'block';
        return;
    }

    grid.style.display = 'grid';
    noResults.style.display = 'none';

    grid.innerHTML = filteredRepos.map((repo, index) => {
        const repoName = repo.url.split('/').slice(-2).join('/');

        return `
            <div class="repo-card fade-in" onclick="openRepository('${repo.url}')" style="animation-delay: ${index * 0.05}s">
                <div class="repo-header">
                    <div class="repo-name">${repoName}</div>
                    <div class="repo-stars">${parseInt(repo.stars).toLocaleString()}</div>
                </div>
                <div class="repo-description">${repo.description || 'No description available'}</div>
                <div class="repo-meta">
                    <div style="display: flex; align-items: center; flex-wrap: wrap;">
                        <div class="repo-language">${repo.language}</div>
                        ${getEffectiveCategory(repo) ? `<div class="repo-category">${getEffectiveCategory(repo)}</div>` : ''}
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function updateStats(repos = allRepos) {
    const totalRepos = repos.length;
    const totalStars = repos.reduce((sum, repo) => sum + parseInt(repo.stars), 0);
    const avgStars = totalRepos > 0 ? Math.round(totalStars / totalRepos) : 0;

    document.getElementById('totalRepos').textContent = totalRepos.toLocaleString();
    document.getElementById('totalStars').textContent = totalStars.toLocaleString();
    document.getElementById('avgStars').textContent = avgStars.toLocaleString();
}

function updateLastReviewDate() {
    if (allRepos.length === 0) return;

    // Find the most recent last_update date
    const mostRecentDate = allRepos.reduce((latest, repo) => {
        const repoDate = new Date(repo.last_update);
        return repoDate > latest ? repoDate : latest;
    }, new Date(0));

    // Format the date in a readable format
    const formattedDate = mostRecentDate.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });

    document.getElementById('lastReviewDate').textContent = formattedDate;
}

function openRepository(url) {
    window.open(url, '_blank');
}

function populateCategoryFilter() {
    const categoryFilter = document.getElementById('categoryFilter');

    // Clear existing options except the first one
    categoryFilter.innerHTML = '<option value="">📂 All Categories</option>';

    // Create array of categories with their counts
    const categoriesWithCounts = allCategories.map(category => {
        const repoCount = allRepos.filter(repo => getEffectiveCategory(repo) === category).length;
        return { category, count: repoCount };
    }).filter(item => item.count > 0); // Only include categories that have repositories

    // Sort categories by count in descending order (highest count first)
    categoriesWithCounts.sort((a, b) => b.count - a.count);

    // Add sorted category options
    categoriesWithCounts.forEach(item => {
        const option = document.createElement('option');
        option.value = item.category;
        option.textContent = `${item.category} (${item.count})`;
        categoryFilter.appendChild(option);
    });
}

function populateLanguageFilter() {
    const languageFilter = document.getElementById('languageFilter');

    // Clear existing options except the first one
    languageFilter.innerHTML = '<option value="">☕ All Languages</option>';

    // Create array of languages with their counts
    const languagesWithCounts = allLanguages.map(language => {
        const repoCount = allRepos.filter(repo => repo.language === language).length;
        return { language, count: repoCount };
    }).filter(item => item.count > 0); // Only include languages that have repositories

    // Sort languages by count in descending order (highest count first)
    languagesWithCounts.sort((a, b) => b.count - a.count);

    // Add sorted language options
    languagesWithCounts.forEach(item => {
        const option = document.createElement('option');
        option.value = item.language;
        option.textContent = `${item.language} (${item.count})`;
        languageFilter.appendChild(option);
    });
}

function filterRepositories() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const selectedCategory = document.getElementById('categoryFilter').value;
    const selectedLanguage = document.getElementById('languageFilter').value;

    // Update URL parameters
    updateUrlParams(
        document.getElementById('searchInput').value,
        selectedCategory,
        selectedLanguage
    );

    filteredRepos = allRepos.filter(repo => {
        // Text search filter
        const repoName = repo.url.split('/').slice(-2).join('/').toLowerCase();
        const description = (repo.description || '').toLowerCase();
        const language = repo.language.toLowerCase();
        const effectiveCategory = getEffectiveCategory(repo);
        const category = (effectiveCategory || '').toLowerCase();

        const matchesSearch = searchTerm === '' ||
            repoName.includes(searchTerm) ||
            description.includes(searchTerm) ||
            language.includes(searchTerm) ||
            category.includes(searchTerm);

        // Category filter
        const matchesCategory = selectedCategory === '' || effectiveCategory === selectedCategory;

        // Language filter
        const matchesLanguage = selectedLanguage === '' || repo.language === selectedLanguage;

        return matchesSearch && matchesCategory && matchesLanguage;
    });

    displayRepositories();
    updateStats(filteredRepos);
}

// Keep the old function name for backward compatibility
function searchRepositories() {
    filterRepositories();
}

// Handle browser back/forward navigation
function handlePopState() {
    loadFiltersFromUrl();
    filterRepositories();
}

// Event listeners
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('searchInput').addEventListener('input', filterRepositories);
    document.getElementById('categoryFilter').addEventListener('change', filterRepositories);
    document.getElementById('languageFilter').addEventListener('change', filterRepositories);

    // Handle browser back/forward navigation
    window.addEventListener('popstate', handlePopState);

    // Load repositories when page loads
    loadRepositories();
});
